package graph

/**
  * Every graph consist of nodes and edges
  * Every edge points to its start end endNode (of custom node type)
  * Every edge should have a label (implemented using dependency injection)
  */
trait Edge[N <: Node[_]] {
  this : Label => //dependency injection : every Edge should have a Label

  val startNode : N //public since it defines the edge
  val endNode : N //public since it defines the edge

}
