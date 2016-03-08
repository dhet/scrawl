package graph

/**
  * Created by nicohein on 06/03/16.
  */
trait Edge[N <: Node[_]] {
  this : Label => //dependency injection : every Edge should have a Label

  val startNode : N //public since it defines the edge
  val endNode : N //public since it defines the edge

}
