package Webgraph

import AbstractGraph.AbstractEdge

/**
  * Created by nicohein on 29/02/16.
  */
class Weblink ( substartNode : Webpage,
                subendNode : Webpage,
                sublabel : LinkLabel = LinkLabel()) extends AbstractEdge[Webpage](substartNode, subendNode, sublabel){

  /**
    *
    * @return tring containing xml description of the Weblink
    */
  def toXML(): String = {
    var xml : String = ""
    xml += s"<weblink>"
    xml += s"<startnode>${startNode.url}</startnode>"
    xml += s"<endnode>${endNode.url}</ednode>"
    xml += sublabel.toXML()
    xml += s"</weblink>"
    xml
  }
}

object Weblink {
  def apply(startNode : Webpage, endNode : Webpage) : Weblink = new Weblink(startNode, endNode)
  def apply(startNode : Webpage, endNode : Webpage, label : LinkLabel) : Weblink  = new Weblink(startNode, endNode, label)
}

