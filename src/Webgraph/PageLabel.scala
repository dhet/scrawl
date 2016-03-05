package Webgraph

import AbstractGraph.Label


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
  override def toXML() : String = {
    var xml : String = ""
    if(this.nonEmpty) {
      xml += s"<pagelabel>"
      for ((key, value) <- this) {
        xml += s"<key>${key.toString}</key>"
        xml += s"<value>${value.toString}</value>"
      }
      xml += s"</pagelabel>"
    }
    xml
  }
}

object PageLabel{
  def apply() : PageLabel = new PageLabel()
}