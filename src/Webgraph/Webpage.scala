package Webgraph

import Graph.Node

/**
  * Created by nicohein on 29/02/16.
  */
class Webpage ( val url : String,
                var content : String,
                label : PageLabel,
                val crawled : Boolean = false,
                edges :  List[Weblink] = Nil,
                visited : Boolean = false,
                level : Int = 0 ) extends Node[Weblink](edges, visited, level, label) {



  def toXML() : String = ???
}

