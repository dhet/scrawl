package webgraph

import abstractgraph.Label


/**
  * Created by nicohein on 01/03/16.
  * The value list contains results of analysis including
  * Flesh-Kincaid Index
  */
class PageLabel extends Label{

  /**
    *
    * @return string containing xml description of the label
    */
  def toXML() : String = {
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

object PageLabel{
  def apply() : PageLabel = new PageLabel()
}