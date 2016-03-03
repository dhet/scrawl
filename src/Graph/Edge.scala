package Graph


/**
  * Created by nicohein on 29/02/16.
  */
abstract class Edge[N](
                     var startNode : N,
                     var endNode : N,
                     var label : Label = new Label) {
}
