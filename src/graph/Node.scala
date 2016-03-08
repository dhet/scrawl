package graph

/**
  * Created by nicohein on 06/03/16.
  */
trait Node[E <: Edge[_]]{
  this : Label =>

  protected[graph] var edges : Set[E] = Set[E]() //set as field since this is only internal structure like double linked list

  private[graph] var visited : Boolean = false

  /**
    * Adds an outgoing Edge to the Node (no integrity check here)
    * @param edge edge to be adde to node
    */
  protected[graph] def addEdge(edge: E) = {
    edges = edges + edge
  }

  /**
    * Removes an outgoing Edge from the Node (no integrity check here)
    * @param edge edge to be removed from node
    */
  protected[graph] def removeEdge(edge: E) = {
    edges = edges.filter((e) => if(edge.startNode.equals(e.startNode)&& edge.endNode.equals(e.endNode)) false else true)
  }
}
