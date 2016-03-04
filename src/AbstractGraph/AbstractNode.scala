package AbstractGraph

/**
  * Created by nicohein on 29/02/16.
  */
abstract class AbstractNode[E](
                                var edges : List[E] = Nil,
                                var visited : Boolean = false,
                                var level : Int = 0,
                                val label : Label = new Label) {

  def addEdge(edge: E) = {
    edges = edge :: edges
  }

  def removeEdge(edge: E) = {
    var i : Int = 1
    for(e <- edges){
      if(edge.equals(e))
      edges = edges.drop(i)
      i += 1
    }
  }

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
