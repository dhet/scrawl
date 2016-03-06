package abstractGraph

/**
  * Created by nicohein on 06/03/16.
  */
trait LabeledObject {
  val label : Label = new Label()

  /**
    *
    * @param labelEntry labelentry to be added
    * @return
    */
  def addLabelEntry(labelEntry: LabelEntry) = {
    label.+=((labelEntry.key, labelEntry.value))
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
    * @param labelEntry labelentry to be updated
    * @return
    */
  def updateLabelEntry(labelEntry: LabelEntry) = {
    if(label.contains(labelEntry.key)){
      label.update(labelEntry.key, labelEntry.value)
    }else{
      addLabelEntry(labelEntry)
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
