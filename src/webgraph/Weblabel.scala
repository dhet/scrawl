package webgraph


import graph.{LabelEntry, Label}

import scala.collection.mutable

/**
  * Created by nicohein on 01/03/16.
  */


trait Weblabel extends Label{
  override val label : mutable.Set[LabelEntry] = mutable.Set[LabelEntry]()

  /**
    * Generates XML corresponding to the Label
    * @return string containing xml description of the label
    */

  def labelToXML() : String = {
    var xml : String = ""
    if(label.nonEmpty) {
      xml += s"<label>"
      for (labelentry <- label) {
        xml += s"<key>${labelentry.key.toString}</key>"
        xml += s"<value>${labelentry.value.toString}</value>"
      }
      xml += s"</label>"
    }
    xml
  }
}