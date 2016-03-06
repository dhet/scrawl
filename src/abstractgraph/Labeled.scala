package abstractgraph

/**
  * Created by nicohein on 06/03/16.
  */
trait Labeled[L <: Label] {
  protected var label : L 

  /**
    *
    * @param labelEntry labelentry to be added
    * @return
    */
  def addLabelEntry(labelEntry: LabelEntry) = {
    label.label += labelEntry
  }

  /**
    *
    * @param key key of the label to look up
    * @return value relatd to the key
    */
  def getLabelEntry(key : String): Any = {
    for(labelentry <- label.label){
      if(labelentry.key.equals(key)){
        return labelentry.value
      }
    }
    AnyRef
  }

  /**
    *
    * @param labelEntry labelentry to be updated
    * @return
    */
  def updateLabelEntry(labelEntry: LabelEntry) = {
    removeLabelEntry(labelEntry.key)
    addLabelEntry(labelEntry)
  }

  /**
    *
    * @param key key of the label to be removed
    * @return
    */
  def removeLabelEntry(key : String) = {
    for(labelentry <- label.label){
      if(labelentry.key.equals(key)){
        label.label -= labelentry
      }
    }
  }
}
