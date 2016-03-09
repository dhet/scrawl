package webgraph

import analyze.AnalyzeURL
import graph.Graph


/**
  * Created by nicohein on 29/02/16.
  */
class Webgraph(val root : Webpage) extends Graph[Webpage, Weblink] {

  addNode(root)

  /**
    * gives the XML version of the graph
    *
    * @return xml
    */
  def xml =
    <webgraph>
      { for (node <- nodes) yield
        {node.xml}
      }
    </webgraph>


  /**
    * Returns the most likely site structure based on dijkstra with a custom url-distance function
    *
    * @return recursive xml
    */
  def siteStructure = {
    runWeightedDijkstra(root, (link) => (AnalyzeURL.distance(link.startNode.url, link.endNode.url)*100).asInstanceOf[Int] )
    <sitestructure>
      {root.subStructure}
    </sitestructure>
  }



  /**
    * Adds a weblink to the graph
    *
    * @param weblink weblink to be added
    * @return this
    */
  def addWeblink(weblink : Weblink) : Weblink = {
    var tempStartNode = weblink.startNode
    var tempEndNode = weblink.endNode
    for(webpage <- nodes){
      if(tempStartNode.url.equals(webpage.url)){
        tempStartNode = webpage.mergeWith(weblink.startNode)
      }
      if(tempEndNode.url.equals(webpage.url)){
        tempEndNode = webpage.mergeWith(weblink.endNode)
      }
    }
    val newLink = new Weblink(tempStartNode, tempEndNode)
    addEdge(newLink)
    newLink
  }

  /**
    * Removes an weblink from the graph
    *
    * @param weblink weblink to be removed
    * @return this
    */
  def removeWeblink(weblink: Weblink) : Graph[Webpage, Weblink] = {
    for(edge <- edges){
      if(weblink.startNode.url.equals(edge.startNode.url) && weblink.endNode.url.equals(edge.endNode.url))
        removeEdge(edge)
    }
    this
  }

  /**
    * Counts the number of uncrawled pages / is equal to links deaper than specified crawl level
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
}

/**
  * Factory for Webgraphs
 */
object Webgraph {
  def apply(root: Webpage) = new Webgraph(root)
}
