package Graph

/**
  * Created by nicohein on 29/02/16.
  */
abstract class Node[E](
                     var edges : List[E] = Nil,
                     var visited : Boolean = false,
                     var level : Int = 0,
                     var label : Label = new Label) {

  def addEdge(edge: E): Node[E] = {
    edges = edge :: edges
    this
  }
  def removeEdge(edge: E): Node[E] = {
    var temp = edges
    var checked: List[E] = Nil
    while (temp.tail.nonEmpty) {
      if (!temp.head.equals(edge)) {
        checked = temp.head :: checked
        temp = temp.tail
      }
      edges = checked ::: temp
    }
    this
  }

}
