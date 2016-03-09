package webgraph

import analyze.AnalyzeURL
import graph.Graph


/**
  * A Webgraph is an implemetation of a Graph
  * A webgraph has an root in addition to the properties: connected, directed and unweighted
  */
class Webgraph(val root : Webpage) extends Graph[Webpage, Weblink] {

  addNode(root)

  /**
    * Returns the XML version of the complete graph
    * @return xml
    */
  def xml =
    <webgraph>
      { for (node <- breadthFirstTraversal(root)) yield //breadth first traversal puts a bit order into the output
        {node.xml}
      }
    </webgraph>

  /**
    * Returns the most likely site structure based on dijkstra with a custom url-distance function
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
    * @param weblink weblink to be added
    * @return this
    */
  def addWeblink(weblink : Weblink) : Weblink = {
    //the following is necessary due to the fact that a Sets + operator uses == to compare objects... thus an equals functuion did not work
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
    weblink.label.foreach((labelEntry) => newLink.addLabelEntry(labelEntry)) //merges the new with the given weblink
    addEdge(newLink)
    newLink
  }

  /**
    * Removes an weblink from the graph
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
