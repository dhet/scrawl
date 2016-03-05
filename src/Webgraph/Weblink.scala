package Webgraph

import AbstractGraph.AbstractEdge

/**
  * Created by nicohein on 29/02/16.
  */
class Weblink ( override val startNode : Webpage,
                override val endNode : Webpage,
                override val label : LinkLabel = LinkLabel()) extends AbstractEdge[Webpage](startNode, endNode, label){

  /**
    *
    * @return tring containing xml description of the Weblink
    */
  def toXML(): String = {
    var xml : String = ""
    xml += s"<weblink>"
    xml += s"<startnode>${startNode.url}</startnode>"
    xml += s"<endnode>${endNode.url}</ednode>"
    xml += label.toXML()
    xml += s"</weblink>"
    xml
  }
}

object Weblink {
  def apply(startNode : Webpage, endNode : Webpage) : Weblink = new Weblink(startNode, endNode)
  def apply(startNode : Webpage, endNode : Webpage, label : LinkLabel) : Weblink  = new Weblink(startNode, endNode, label)
}

