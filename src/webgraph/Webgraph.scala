package webgraph

import graph.Graph


/**
  * Created by nicohein on 29/02/16.
  */
class Webgraph(val root : Webpage) extends Graph[Webpage, Weblink] {

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
  def addWeblink(weblink : Weblink) : Weblink = {
    var tempstartnode = weblink.startNode
    var tempendnode = weblink.endNode
    for(webpage <- nodes){
      if(tempstartnode.url.equals(webpage.url)){
        tempstartnode = webpage.merge(weblink.startNode)
      }
      if(tempendnode.url.equals(webpage.url)){
        tempendnode = webpage.merge(weblink.endNode)
      }
    }
    addEdge(new Weblink(tempstartnode, tempendnode))
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

}


object Webgraph {
  def apply(root: Webpage) = new Webgraph(root)
}
