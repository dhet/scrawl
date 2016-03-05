package AbstractGraph

/**
  * Created by nicohein on 29/02/16.
  */
abstract class AbstractNode[E](val label : Label = Label()) {

  var edges : Set[E] = Set[E]() //set as field since this is only internal structure like double linked list
  var visited : Boolean = false

  /**
    *
    * @param edge edge to be adde to node
    */
  def addEdge(edge: E) = {
    edges = edges.+(edge)
  }

  /**
    *
    * @param edge edge to be removed from node
    */
  def removeEdge(edge: E) = {
    edges = edges.-(edge)
  }

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
