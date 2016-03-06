package webgraph

import abstractGraph.Label

/**
  * Created by nicohein on 01/03/16.
  */
class LinkLabel extends Label{

  /**
    *
    * @return string containing xml description of the label
    */
  def toXML() : String = {
    var xml : String = ""
    if(this.nonEmpty) {
      xml += s"<linklabel>"
      for ((key, value) <- this) {
        xml += s"<key>${key.toString}</key>"
        xml += s"<value>${value.toString}</value>"
      }
      xml += s"</linklabel>"
    }
    xml
  }

}

object LinkLabel {
  def apply() : LinkLabel = new LinkLabel()
}

