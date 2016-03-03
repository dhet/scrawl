package Webgraph

import Graph.Graph

import scala.collection.mutable


/**
  * Created by nicohein on 29/02/16.
  */
class Webgraph(root : Webpage) extends Graph[Webpage, Weblink] {


  def addWebpage(webpage : Webpage) : Webgraph = {
    addNode(webpage)
    this
  }

  def countUncrawled() : Int = {
    var count : Int  = 0
    for(node <- breadthFirstTraversal(root)){
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
    throw new Exception //better implement with...*/
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


  override def addNode(node: Webpage): Graph[Webpage, Weblink] = {
    nodes = nodes + node
    this
  }

  override def addEdge(edge: Weblink): Graph[Webpage, Weblink] = {
    edges = edges + edge
    //add edges to nodes
    edge.startNode.addEdge(edge)
    edge.endNode.addEdge(edge)
    //add nodes of edge if not already existing
    addNode(edge.startNode)
    addNode(edge.endNode)
    this
  }

  override def depthFirstTraversal(node: Webpage): List[Webpage] = {
    var pagelist : List[Webpage] = Nil
    node.visited = true

    for(child : Webpage <- node.edges.map((e) => e.endNode)){
      if(!child.visited)
        pagelist = child :: pagelist
        pagelist = pagelist ::: depthFirstTraversal(child)
    }
    pagelist
  }

  override def breadthFirstTraversal(node: Webpage): List[Webpage] = {
    var pagelist : List[Webpage] = Nil
    val queue : mutable.Queue[Webpage] = mutable.Queue[Webpage]()
    queue.enqueue(node)
    node.visited = true


    while(queue.nonEmpty){
      val temp = queue.dequeue()
      pagelist = temp :: pagelist
      for(child : Webpage <- temp.edges.map((e) => e.endNode)){
        if(!child.visited){
          queue.enqueue(child)
          child.visited = true
        }
      }
    }
    pagelist

  }

  override def removeNode(node: Webpage): Graph[Webpage, Weblink] = {
    //remove all edges of node, then remove node
    for(edge : Weblink <- node.edges){
      removeEdge(edge)
    }
    nodes = nodes - node
    this
  }

  override def removeEdge(edge: Weblink): Graph[Webpage, Weblink] = {
    //it is not necessary to remove the edges from all nodes
    edge.startNode.removeEdge(edge)
    edge.endNode.removeEdge(edge)
    //now remove edge from graph
    edges = edges - edge
    this
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
