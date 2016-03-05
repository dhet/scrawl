package AbstractGraph


/**
  * Created by nicohein on 29/02/16.
  */
abstract class AbstractEdge[N](
                     val startNode : N,
                     val endNode : N,
                     label : Label = new Label) {


  /**
    *
    * @param key key of the label to be added
    * @param value value of the label to be added
    * @return
    */
  def addLabelEntry(key : String, value : Any) = {
    label.+=((key, value))
  }

  /**
    *
    * @param key key of the label to look up
    * @return value relatd to the key
    */
  def getLabelEntry(key : String): Any = {
    label.get(key).get
  }

  /**
    *
    * @param key key of the label to be updated
    * @param value new value assigned to the key
    * @return
    */
  def updateLabelEntry(key : String, value : Any) = {
    if(label.contains(key)){
      label.update(key, value)
    }else{
      addLabelEntry(key, value)
    }
  }

  /**
    *
    * @param key key of the label to be removed
    * @return
    */
  def removeLabelEntry(key : String) = {
    label.-=(key)
  }
}
