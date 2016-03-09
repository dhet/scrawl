package graph

/**
  * Every graph consist of nodes and edges
  * Every node points to all outgoing edges (of custom edge type) for fast iterations on the graph
  * Every edge should have a label (implemented using dependency injection)
  */
trait Node[E <: Edge[_]]{
  this : Label =>

  protected[graph] var outgoingEdges: Set[E] = Set[E]() //set as field since this is only internal structure like double linked list

  private[graph] var visited : Boolean = false

  /**
    * Adds an outgoing Edge to the Node (no integrity check here)
    * @param edge edge to be adde to node
    */
  protected[graph] def addOutgoingEdge(edge: E) = {
    outgoingEdges = outgoingEdges + edge
  }

  /**
    * Removes an outgoing Edge from the Node (no integrity check here)
    * @param edge edge to be removed from node
    */
  protected[graph] def removeOutgoingEdge(edge: E) = {
    outgoingEdges = outgoingEdges.filter((anEdge) => {
      !(edge.startNode.equals(anEdge.startNode) && edge.endNode.equals(anEdge.endNode)) //compares two edges
    })
  }

}
