package webgraph

import java.net.URL


import graph.{LabelEntry, Node}

/**
  * Created by nicohein on 29/02/16.
  */
class Webpage ( val url : URL,
                var content : String = "",
                var crawled : Boolean = false) extends Node[Weblink] with Weblabel{

  /**
    * Generates XML corresponding to the Webpage
    * @return tring containing xml description of the Webpage
    */
  def toXML() : String = {
    var xml : String = ""
    xml += s"<webpage>"
    xml += s"<url>${url.toString}</url>"
    xml += labelToXML()
    xml += s"<crawled>$crawled</crawled>"
    if(edges.nonEmpty){
      xml += s"<edges>"
      for(url <- edges.map((weblink) => weblink.endNode.url)){
        xml += s"<outlink>${url.toString}</outlink>"
      }
      xml += s"</edges>"
    }
    xml += s"</webpage>"
    xml
  }
  /**
    * Runs a provided sequence of Analyzes on the Webpage (Node) and stores them in the Label
    * @param algorithms Algorithms to analyze the Node
    */
  def analyze(algorithms : Seq[(Webpage) => LabelEntry]) = algorithms.foreach(alg => addLabelEntry(alg(this)))
}

object Webpage {

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

