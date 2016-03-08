package graph

import scala.collection.mutable

/**
  * Created by nicohein on 06/03/16.
  */
trait Label{
  protected val label : mutable.Set[LabelEntry] //access only via defined methods

  /**
    *
    * @param labelEntry labelentry to be added
    * @return
    */
  def addLabelEntry(labelEntry: LabelEntry) : Label = {
    label.add(labelEntry)
    this
  }

  /**
    *
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
    *
    * @param labelEntry labelentry to be updated
    * @return
    */
  def updateLabelEntry(labelEntry: LabelEntry) : Label = {
    removeLabelEntry(labelEntry.key)
    addLabelEntry(labelEntry)
    this
  }

  /**
    *
    * @param key key of the label to be removed
    * @return
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