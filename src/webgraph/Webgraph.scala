package webgraph

import graph.Graph


/**
  * Created by nicohein on 29/02/16.
  */
class Webgraph(root : Webpage) extends Graph[Webpage, Weblink] {

  nodes = nodes + root

  /**
    * gives the XML version of the graph
    * @return xml
    */
  def xml =
    <webgraph>
      { for (node <- nodes) yield
      {node.xml}
      }
    </webgraph>

  /**
    * Adds a weblink to the graph
    * @param weblink weblink to be added
    * @return this
    */
  def addWeblink(weblink : Weblink) : Graph[Webpage, Weblink] = {
    addEdge(weblink)
  }

  /**
    * Removes an weblink from the graph
    * @param weblink weblink to be removed
    * @return this
    */
  def removeWeblink(weblink: Weblink) : Graph[Webpage, Weblink] = {
    removeEdge(weblink)
  }

  /**
    * Counts the number of uncrawled pages / is equal to links deaper than specified crawl level
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
    *
    * @return generates sitemap of the crawled page
    */
  def generateSitemap() : List[String] = {
    depthFirstTraversal(root).map((node: Webpage) => node.url.toString)
  }

}


object Webgraph {
  def apply(root: Webpage) = new Webgraph(root)
}
