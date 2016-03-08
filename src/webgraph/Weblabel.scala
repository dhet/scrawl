package webgraph


import graph.{LabelEntry, Label}

import scala.collection.mutable

/**
  * Created by nicohein on 01/03/16.
  */


trait Weblabel extends Label{

  override val label : mutable.Set[LabelEntry] = mutable.Set[LabelEntry]()

  def labelxml =
    {for (labelentry <- label) yield
        <label key={labelentry.key.toString} value={labelentry.value.toString}/>
    }

  /**
    * Generates XML corresponding to the Label
    * @return string containing xml description of the label
    */


  def labelToXML() : String = {
    var xml : String = ""
    if(label.nonEmpty) {
      xml += s"<labels>"
      for (labelentry <- label) {
        xml += s"<${labelentry.key.toString}>${labelentry.value.toString}</${labelentry.key.toString}>"
      }
      xml += s"</labels>"
    }
    xml
  }
}
