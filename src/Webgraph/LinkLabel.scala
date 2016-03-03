package Webgraph

import Graph.Label

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

  def toXML() : String = {
    var xml : String = ""
    for((key, value) <- this){
      xml += s"<LinkLabel>"
      xml += s"  <key>${key.toString}</key>"
      xml += s"  <value>${value.toString}</value>"
      xml += s"</LinkLabel>"
    }
    xml
  }


}

