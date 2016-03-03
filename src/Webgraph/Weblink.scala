package Webgraph

import Graph.Edge

/**
  * Created by nicohein on 29/02/16.
  */
class Weblink ( startNode : Webpage,
                endNode : Webpage,
                label : LinkLabel = new LinkLabel) extends Edge[Webpage](startNode, endNode, label){


  def toXML() : String = ???
}

