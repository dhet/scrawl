package Graph

/**
  * Created by nicohein on 29/02/16.
  */
abstract class Graph[N, E] {
  var nodes: Set[N] = Set[N]()
  var edges: Set[E] = Set[E]()


  def addNode(node : N) : Graph[N, E]

  def addEdge(edge : E): Graph[N, E]

  def removeNode(node : N): Graph[N, E]

  def removeEdge(edge : E): Graph[N, E]

  def depthFirstTraversal(node : N ) : List[N]

  def breadthFirstTraversal(node : N ) : List[N]

}