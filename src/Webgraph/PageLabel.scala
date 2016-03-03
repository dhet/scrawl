package Webgraph

import AbstractGraph.Label


/**
  * Created by nicohein on 01/03/16.
  */
class PageLabel extends Label{
  /**
    * The value list contains results of analysis including
    * Flesh-Kincaid Index
    *
    */

  override def toXML() : String = {
    var xml : String = ""
    xml += s"<pagelabel>"
    for((key, value) <- this){
      xml += s"<key>${key.toString}</key>"
      xml += s"<value>${value.toString}</value>"
    }
    xml += s"</pagelabel>"
    xml
  }
}

object PageLabel{
  def apply() : PageLabel = new PageLabel()
}