package Webgraph

import AbstractGraph.Label

/**
  * Created by nicohein on 01/03/16.
  */
class LinkLabel extends Label{
  /**
    * The value list contails results of analysis including
    * Hyperlink hierachie
    * Betweeness
    * InLink / OffLing - Link to other domain or same domain
    */

  /**
    *
    * @return string containing xml description of the label
    */
  override def toXML() : String = {
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

