package webgraph

import java.net.URL

import graph.{LabelEntry, Node}

/**
  * Created by nicohein on 29/02/16.
  */
class Webpage ( val url : URL,
                var content : String = "",
                var crawled : Boolean = false) extends Node[Weblink] with Weblabel{

  def xml =
    <webpage url={url.toString} crawled={crawled.toString}>{labelxml}
      {for (edge <- edges) yield <link url={edge.endNode.url.toString}/>}
    </webpage>

  /**
    * Runs a provided sequence of Analyzes on the Webpage (Node) and stores them in the Label
    * @param algorithms Algorithms to analyze the Node
    */
  def analyze(algorithms : Seq[(Webpage) => Option[LabelEntry]]) = {
    algorithms.foreach(alg => {
      val opt = alg(this)
      if(opt.isDefined) addLabelEntry(opt.get)
    })
    crawled = true
  }
}

case class ExternalWebpage(override val url : URL) extends Webpage(url)
case class InternalWebpage(override val url : URL) extends Webpage(url)


object Webpage {
  def apply(url : String, parent : URL) : Option[Webpage] =  {
    if(url.startsWith("/") || url.contains(parent.getHost)){
      Some(InternalWebpage(new URL(parent, url)))
    } else{
      try{
        Some(ExternalWebpage(new URL(url)))
      } catch{
        case e : Exception => None
      }
    }
  }

  /**
    * Apply Function that takes an URL as parameter
    * @param url defines a webpage
    * @return returns a new webpage
    */
  def apply(url : URL) : Webpage = new Webpage(url)

  /**
    * Apply Function that takes an URL and page content as parameter
    * @param url defines a webpage
    * @param content content of the crawled page
    * @return returns a new webpage object
    */
  def apply(url : URL, content : String ) : Webpage = new Webpage(url, content)

  /**
    * Apply Function that takes an URL and page content as parameter
    * @param url defines a webpage
    * @param content content of the crawled page
    * @param crawled flags if the page is already crawled or not
    * @param label label of the webpage
    * @return returns a new webpage object
    */
  def apply(url : URL, content : String, crawled : Boolean, label : Weblabel) : Webpage = {
    var webpage = new Webpage(url, content, crawled)
    for(labelentry <- label.label){
      webpage.addLabelEntry(labelentry)
    }
    webpage
  }

}

