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
    * Returns the result of the analyzes in a plane structure
    * @return xml
    */
  def xml =
    <webpage url={url.toString} crawled={crawled.toString}>{labelxml}
      <links>
      {for (edge <- outgoingEdges) yield <link url={edge.endNode.url.toString}/>}
      </links>
    </webpage>

  /**
    * Returns the most likely site structure based on dijkstra with a custom url-distance function
    * @return recursive xml
    */
  def subStructure : Elem  = {
    <webpage url={url.toString}>
      {for (n <- outgoingEdges.map((e) => e.endNode)
            if n.getLabelEntry("parent").get.asInstanceOf[Webpage].equals(this) //The label "parent" is set by during dijkstra to be able to reconstruct paths
            if !n.url.getPath.equals(url.getPath)) //to prevent the circles - they should not occur with dijkstra .. however... testing required
      yield n.subStructure}
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
  def mergeWith(webpage : Webpage): Webpage = {
    if(webpage.url.equals(url)){
      //replace content if this page has none otherwise vorget new content
      if(content.equals(""))
        content = webpage.content
      for(labelentry <- webpage.label){
       updateLabelEntry(labelentry)
      }
      for(edge <- webpage.outgoingEdges){
        addOutgoingEdge(edge)
      }
    }
    this
  }

  /**
    * Gives a shortenes string with essential data of the object...
    * @return
    */
  override def toString() : String = s"Webpage(url:${url.toString()}, ${outgoingEdges.toString()})"
}

/**
  * Factory Object for Webpages
  */
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
    val webpage = new Webpage(url, content, crawled)
    for(labelentry <- label.label){
      webpage.addLabelEntry(labelentry)
    }
    webpage
  }

}

