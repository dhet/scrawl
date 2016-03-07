package webgraph


import graph.{LabelEntry, Label}

import scala.collection.mutable

/**
  * Created by nicohein on 01/03/16.
  */
trait LinkLabel extends Label{
  override val label : mutable.Set[LabelEntry] = mutable.Set[LabelEntry]()

  /**
    *
    * @return string containing xml description of the label
    */
  def linkToXML() : String = {
    var xml : String = ""
    if(label.nonEmpty) {
      xml += s"<linklabel>"
      for (labelentry <- label) {
        xml += s"<key>${labelentry.key.toString}</key>"
        xml += s"<value>${labelentry.value.toString}</value>"
      }
      xml += s"</linklabel>"
    }
    xml
  }
}
