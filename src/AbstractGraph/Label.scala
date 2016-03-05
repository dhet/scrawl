package AbstractGraph

import scala.collection.mutable

/**
  * Created by nicohein on 02/03/16.
  */
class Label extends mutable.ListMap[String, Any] {

  /**
    *
    * @return string containing xml description of the label
    */
  def toXML() : String = {
    var xml : String = ""
      if(this.nonEmpty){
      xml += s"<label>"
      for((key, value) <- this){
        xml += s"<key>${key.toString}</key>"
        xml += s"<value>${value.toString}</value>"
      }
      xml += s"</label>"
    }
    xml
  }
}

object Label {
  def apply() = new Label
}
