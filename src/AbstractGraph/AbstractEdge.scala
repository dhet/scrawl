package AbstractGraph


/**
  * Created by nicohein on 29/02/16.
  */
abstract class AbstractEdge[N](
                     val startNode : N,
                     val endNode : N,
                     val label : Label = new Label) {

  def addLabelEntry(key : String, value : Any) = {
    label.+=((key, value))
  }
  def getLabelEntry(key : String): Any = {
    label.get(key).get
  }
  def updateLabelEntry(key : String, value : Any) = {
    if(label.contains(key)){
      label.update(key, value)
    }else{
      addLabelEntry(key, value)
    }
  }
  def removeLabelEntry(key : String) = {
    label.-=(key)
  }
}
