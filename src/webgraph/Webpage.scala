package webgraph

import java.net.URL

import graph.{LabelEntry, Node}

import scala.xml.Elem

/**
  * Created by nicohein on 29/02/16.
  */
class Webpage ( val url : URL,
                var content : String = "",
                var crawled : Boolean = false) extends Node[Weblink] with Weblabel{

  /**
    * Returns the resurlt of the analyzes in a plane structure
    * @return xml
    */
  def xml =
    <webpage url={url.toString} crawled={crawled.toString}>{labelxml}
      <links>
      {for (edge <- edges) yield <link url={edge.endNode.url.toString}/>}
      </links>
    </webpage>

  /**
    * Resturns the most likely sitestructure based on dijkstra with a custom url-distance function
    * @return recursive xml
    */
  def substructure : Elem  = {
    <webpage url={url.toString}>
      {for (n <- edges.map((e) => e.endNode);
            //The label "parent" is set by during dijkstra to be able to reconstruct paths
            if(n.getLabelEntry("parent").get.asInstanceOf[Webpage].equals(this));
            if !n.url.getPath.equals(url.getPath)) //to prevent the circles - they should not occur with dijkstra .. however... testing required
      yield n.substructure}
    </webpage>
  }

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

  /**
    * Merges two webpages with the same urls (the ID) to prevent double occurence of webpages is the graph
    * @param webpage the webpage which should be merges into this
    * @return this
    */
  def merge(webpage : Webpage): Webpage = {
    if(webpage.url.equals(url)){
      //replace content if this page has none otherwise vorget new content
      if(content.equals(""))
        content = webpage.content
      for(labelentry <- webpage.label){
       updateLabelEntry(labelentry)
      }
      for(edge <- webpage.edges){
        addEdge(edge)
      }
    }
    this
  }

  /**
    * Gives a shortenes string with essential data of the object...
    * @return
    */
  @Override
  override def toString() : String = s"Webpage(url:${url.toString()}, ${edges.toString()})"
}

case class ExternalWebpage(override val url : URL) extends Webpage(url)
case class InternalWebpage(override val url : URL) extends Webpage(url)


object Webpage {
  /**
    * Apply function that gives either an Internal or External Webpage
    * @param url (String) url of the new webpage to be constructed
    * @param parent of the Parent
    * @return returns a new internel or external webpage
    */
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

