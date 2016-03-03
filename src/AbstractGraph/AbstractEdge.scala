package AbstractGraph


/**
  * Created by nicohein on 29/02/16.
  */
abstract class AbstractEdge[N](
                     val startNode : N,
                     val endNode : N,
                     val label : Label = new Label) {

  def addLabelEntry(key : String, value : Any): AbstractEdge[N] = {
    label.+=((key, value))
    this
  }
  def getLabelEntry(key : String): Any = {
    label.get(key).get
  }
  def updateLabelEntry(key : String, value : Any) = {
    label.update(key, value)
  }
  def removeLabelEntry(key : String) = {
    label.-=(key)
  }
}
