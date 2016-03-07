package webgraph

import analyze.{Inlink, Outlink}
import graph.Graph


/**
  * Created by nicohein on 29/02/16.
  */
class Webgraph(root : Webpage) extends Graph[Webpage, Weblink] {

  /**
    *
    * @param weblink weblink that should be added to the graph
    */
  def addWeblink(weblink : Weblink) : Graph[Webpage, Weblink] = {
    addEdge(weblink)
  }

  /**
    *
    * @return number of nodes not crawled yet
    */
  def countUncrawledNodes() : Int = {
    var count : Int  = 0
    for(node <- nodes){
      if(!node.crawled)
        count += 1
    }
    count
  }

  /**
    *
    * @return returns the next uncraled webpage found during a breadth first search
    */
  def nextUncrawledNode() : Option[Webpage] = {
    var page = nextPageConstraintBreadthFirst((node : Webpage) => !node.crawled)
    page match {
      case Some(webpage)  =>
        page.get.crawled = true
        page
      case _ => None

    }
  }

  //TODO inefficient (implement next neighbor search in graph)
  def nextPageConstraintBreadthFirst(f :(Webpage) => Boolean) : Option[Webpage] = {
    for(node <- breadthFirstTraversal(root)){
      if(f(node))
        return Some(node)
    }
    None
  }

  //TODO inefficient
  def nextPageContraintDepththFirst(f :(Webpage) => Boolean) : Option[Webpage] = {
    for(node <- depthFirstTraversal(root)){
      if(f(node))
        return Some(node)
    }
    None
  }

  /**
    * analyzes the linktypes (page internal, offpage, mail etc)
    */
  @deprecated("will be implemented using Crawlprefs")
  def analyzeLinktypes() = {
    analyzeEdges("linktype", (weblink: Weblink) =>
      if(weblink.startNode.url.getHost == weblink.endNode.url.getHost)
        new Inlink(weblink.endNode.url)
      else
        new Outlink(weblink.endNode.url))
  }

  /**
    *
    * @param labelkey The key referencing the results in labels
    * @param f f : (E) => Any function on edge analyzing it
    */
  @deprecated("Use analyzeEdges instead")
  def analyzeWeblinks(labelkey: String, f: (Weblink) => Any): Webgraph = {
    analyzeEdges(labelkey, f)
    this
  }

  /**
    *
    * @param labelkey  The key referencing the results in labels
    * @param f f : (N) => Any function on node analyzing it
    */
  @deprecated("Use analyzeNodes instead")
  def analyzeWebpages(labelkey: String, f: (Webpage) => Any): Webgraph = {
    analyzeNodes(labelkey, f)
    this
  }

  /**
    *
    * @return generates sitemap of the crawled page
    */
  def generateSitemap() : List[String] = {
    analyzeLinktypes()
    depthFirstTraversal(root).map((node: Webpage) => node.url.toString)
  }

  //TODO should this method return the xml version of the graph or the crawled page? See generate Sitemap
  /**
    *
    * @return string containing xml description of the webgraph
    */
  def toXML() : String = {
    var xml : String = ""
    xml += s"<Webgraph>"
    for(node <- nodes){
      xml += node.toXML()
    }
    xml += s"</Webgraph>"
    xml
  }

}


object Webgraph {
  def apply(root: Webpage) = new Webgraph(root)
}
