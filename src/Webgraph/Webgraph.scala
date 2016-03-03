package Webgraph

import AbstractGraph.AbstractGraph

import scala.collection.mutable


/**
  * Created by nicohein on 29/02/16.
  */
class Webgraph(root : Webpage) extends AbstractGraph[Webpage, Weblink] {

  def addWeblink(weblink : Weblink) = {
    addEdge(weblink)
  }

  def count() : Int = {
    var count : Int  = 0
    for(node <- nodes){
      count += 1
    }
    count
  }
  def countUncrawled() : Int = {
    var count : Int  = 0
    for(node <- nodes){
      if(!node.crawled)
        count += 1
    }
    count
  }

  def nextUncrawledNode() : Webpage = {
    //wrong
    for(node <- breadthFirstTraversal(root)){
      if(!node.crawled)
        return node
    }
    throw new Exception //better implement with..Furtures*/
  }

  def generateSitemap() : List[String] = {
    setUnvisited()
    depthFirstTraversal(root).map((node: Webpage) => node.url)
  }

  def toXML() : String = {
    var xml : String = ""
    xml += s"<Webgraph>"
    for(node <- nodes){
      xml += node.toXML()
    }
    xml += s"</Webgraph>"
    xml
  }


  override def addNode(node: Webpage) = {
    nodes = nodes + node
  }

  override def addEdge(edge: Weblink) = {
    edges = edges + edge
    //add edges to nodes
    edge.startNode.addEdge(edge)
    edge.endNode.addEdge(edge)
    //add nodes of edge if not already existing
    addNode(edge.startNode)
    addNode(edge.endNode)
  }

  override def depthFirstTraversal(node: Webpage): List[Webpage] = {
    setUnvisited()
    node :: depthFirstTraversalHelper(node)
  }
  private def depthFirstTraversalHelper(node: Webpage): List[Webpage] = {
    var pagelist : List[Webpage] = Nil
    node.visited = true
    for(child : Webpage <- node.edges.map((e) => e.endNode)){
      if(!child.visited){
        pagelist = child :: pagelist
        pagelist = pagelist ::: depthFirstTraversalHelper(child)
      }
    }
    pagelist
  }

  override def breadthFirstTraversal(node: Webpage): List[Webpage] = {
    setUnvisited()
    var pagelist : List[Webpage] = Nil
    val queue : mutable.Queue[Webpage] = mutable.Queue[Webpage]()
    queue.enqueue(node)
    node.visited = true

    while(queue.nonEmpty){
      val tempnode = queue.dequeue()
      pagelist = tempnode :: pagelist
      for(child : Webpage <- tempnode.edges.map((e) => e.endNode)){
        if(!child.visited){
          queue.enqueue(child)
          child.visited = true
        }
      }
    }
    pagelist.reverse

  }

  override def removeNode(node: Webpage) = {
    //remove all edges of node, then remove node
    for(edge : Weblink <- node.edges){
      removeEdge(edge)
    }
    nodes = nodes - node
  }

  override def removeEdge(edge: Weblink) = {
    //it is not necessary to remove the edges from all nodes
    edge.startNode.removeEdge(edge)
    edge.endNode.removeEdge(edge)
    if(edge.startNode.edges.isEmpty)
      removeNode(edge.startNode)
    if(edge.endNode.edges.isEmpty)
      removeNode(edge.endNode)
    //now remove edge from graph
    edges = edges - edge
  }

  private def setUnvisited(): Unit = {
    for(node <- nodes){
      node.visited = false
    }
  }
}


object Webgraph {
  def apply(root: Webpage) = new Webgraph(root)
}
