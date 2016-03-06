package abstractGraph

import scala.collection.mutable


/**
  * Created by nicohein on 02/03/16.
  */
class Label extends mutable.ListMap[String, Any]

object Label extends Label{
  def apply()  = new Label()
}
