package Webgraph

import Graph.Label


/**
  * Created by nicohein on 01/03/16.
  */
class PageLabel extends Label{
  /**
    * The value list contains results of analysis including
    * Flesh-Kincaid Index
    *
    */

  def toXML() : String = {
    var xml : String = ""
    for((key, value) <- this){
      xml += s"<PageLabel>"
      xml += s"  <key>${key.toString}</key>"
      xml += s"  <value>${value.toString}</value>"
      xml += s"</PageLabel>"
    }
    xml
  }
}
