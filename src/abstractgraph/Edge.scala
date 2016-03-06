package abstractgraph

/**
  * Created by nicohein on 06/03/16.
  */
trait Edge[N <: Node[_, _], L <: Label] extends Labeled[L] {

  val startNode : N
  val endNode : N

}
