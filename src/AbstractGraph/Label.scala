package AbstractGraph

import scala.collection.mutable


/**
  * Created by nicohein on 02/03/16.
  */
class Label extends mutable.ListMap[String, Any]

object Label extends Label{
  def apply()  = new Label()

  implicit def Label2ListMap(label : Label) : mutable.ListMap[String, Any] = {
    var listmap : mutable.ListMap[String, Any] = new mutable.ListMap[String, Any]
    for(elem <- label){
      listmap += elem
  }
  listmap
 }

 implicit def ListMap2Label(listmap : mutable.ListMap[String, Any]) : Label = {
   var label : Label = new Label
   for(elem <- listmap){
     label += elem
   }
   listmap
 }
}
