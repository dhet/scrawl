package abstractGraph

/**
  * Created by nicohein on 29/02/16.
  */
abstract class AbstractNode[E](
                                override val label : Label = Label()) extends LabeledObject {

  var edges : Set[E] = Set[E]() //set as field since this is only internal structure like double linked list
  var visited : Boolean = false

  /**
    *
    * @param edge edge to be adde to node
    */
  def addEdge(edge: E) = {
    edges = edges.+(edge)
  }

  /**
    *
    * @param edge edge to be removed from node
    */
  def removeEdge(edge: E) = {
    edges = edges.-(edge)
  }
}
