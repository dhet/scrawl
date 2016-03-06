package abstractgraph

/**
  * Created by nicohein on 06/03/16.
  */
trait Node[E <: Edge[_ , _], L <: Label] extends Labeled[L]{

  var edges : Set[E] = Set[E]() //set as field since this is only internal structure like double linked list
  private[abstractgraph] var visited : Boolean = false

  /**
    *
    * @param edge edge to be adde to node
    */
  private[abstractgraph] def addEdge(edge: E) = {
    edges = edges.+(edge)
  }

  /**
    *
    * @param edge edge to be removed from node
    */
  private[abstractgraph] def removeEdge(edge: E) = {
    edges = edges.-(edge)
  }
}
