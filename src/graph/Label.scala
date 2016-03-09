package graph

import scala.collection.mutable

/**
  * Every label (mixed into all nodes and edges) consists of a list of label entries.
  */
trait Label{
  protected val label : mutable.Set[LabelEntry] //access only via defined methods

  /**
    * Adds a labelentry (key value pair) to a label
    * @param labelEntry labelentry to be added
    * @return
    */
  def addLabelEntry(labelEntry: LabelEntry) : Label = {
    label.add(labelEntry)
    this
  }

  /**
    * returns a Value (encapulated) to a given Key
    * @param key key of the label to look up
    * @return value relatd to the key
    */
  def getLabelEntry(key : String): Option[Any] = {
    for(labelentry <- label){
      if(labelentry.key.equals(key)){
        return Some(labelentry.value)
      }
    }
    None
  }

  /**
    * Updates or Creates a label for a given Labelentry
    * @param labelEntry labelentry to be updated
    * @return this
    */
  def updateLabelEntry(labelEntry: LabelEntry) : Label = {
    removeLabelEntry(labelEntry.key)
    addLabelEntry(labelEntry)
    this
  }

  /**
    * removes a labelentry from the label
    * @param key key of the label to be removed
    * @return this
    */
  def removeLabelEntry(key : String) : Label = {
    for(labelentry <- label){
      if(labelentry.key.equals(key)){
        label.remove(labelentry)
      }
    }
    this
  }
}