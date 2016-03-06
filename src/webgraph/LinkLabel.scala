package webgraph


import abstractgraph.Label

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

object LinkLabel {
  def apply() : LinkLabel = new LinkLabel()
}

