package AbstractGraph

/**
  * Created by nicohein on 29/02/16.
  */
abstract class AbstractGraph[N, E] {
  var nodes: Set[N] = Set[N]()
  var edges: Set[E] = Set[E]()


  def addNode(node : N)

  def addEdge(edge : E)

  def removeNode(node : N)

  def removeEdge(edge : E)

  def depthFirstTraversal(node : N ) : List[N]

  def breadthFirstTraversal(node : N ) : List[N]

}