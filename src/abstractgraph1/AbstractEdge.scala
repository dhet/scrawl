package abstractGraph


/**
  * Created by nicohein on 29/02/16.
  */
abstract class AbstractEdge[N](
                                val startNode : N,
                                val endNode : N,
                                override val label : Label = Label()) extends LabeledObject{
}
