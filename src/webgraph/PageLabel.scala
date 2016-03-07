package webgraph

import graph.{LabelEntry, Label}

import scala.collection.mutable


/**
  * Created by nicohein on 01/03/16.
  * The value list contains results of analysis including
  * Flesh-Kincaid Index
  */
trait PageLabel extends Label{
  override val label : mutable.Set[LabelEntry] = mutable.Set[LabelEntry]()
  /**
    *
    * @return string containing xml description of the label
    */
  def pageToXML() : String = {
    var xml : String = ""
    if(label.nonEmpty) {
      xml += s"<pagelabel>"
      for (labelentry <- label) {
        xml += s"<key>${labelentry.key.toString}</key>"
        xml += s"<value>${labelentry.value.toString}</value>"
      }
      xml += s"</pagelabel>"
    }
    xml
  }
}