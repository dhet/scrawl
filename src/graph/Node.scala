package graph

/**
  * Created by nicohein on 06/03/16.
  */
trait Node[E <: Edge[_]]{
  this : Label =>

  protected[graph] var outgoingEdges: Set[E] = Set[E]() //set as field since this is only internal structure like double linked list

  private[graph] var visited : Boolean = false

  /**
    * Adds an outgoing Edge to the Node (no integrity check here)
 *
    * @param edge edge to be adde to node
    */
  protected[graph] def addOutgoingEdge(edge: E) = {
    outgoingEdges = outgoingEdges + edge
  }

  /**
    * Removes an outgoing Edge from the Node (no integrity check here)
 *
    * @param edge edge to be removed from node
    */
  protected[graph] def removeOutgoingEdge(edge: E) = {
    outgoingEdges = outgoingEdges.filter((anEdge) => !compareEdges(edge, anEdge))
  }

  private def compareEdges(a : E, b : E) : Boolean = {
    a.startNode.equals(b.startNode) && a.endNode.equals(b.endNode)
  }
}
