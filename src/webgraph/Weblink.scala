package webgraph

import graph.{LabelEntry, Edge}

/**
  * Created by nicohein on 29/02/16.
  */
class Weblink ( override val startNode : Webpage,
                override val endNode : Webpage) extends Edge[Webpage] with Weblabel {

  /**
    * Returns the resurlt of the analyzes in a plane structure
    * @return xml
    */
  def xml =
    <weblink source={startNode.url.toString} target={endNode.url.toString}>
      {labelxml}
    </weblink>

  /**
    * Runs a provided sequence of Analyzes on the Weblink (Edge) and stores them in the Label
    * @param algorithms Algorithms to analyze the Edge
    */
  def analyze(algorithms : Seq[(Weblink) => LabelEntry]) = {
    algorithms.foreach(alg => addLabelEntry(alg(this)))
  }

  /**
    * Gives a shortenes string with essential data of the object...
    * @return
    */
  override def toString() : String = s"Edge(startnode:${startNode.url.toString}, endnode:${endNode.url.toString} )"
}

/**
  * Factory Object for Weblinks
  */
object Weblink {
  /**
    * Apply Function that takes linksource and linktarget as parameter
    * @param startNode webpage which is the links source
    * @param endNode webpage which ist the links target
    * @return returns a new weblink object
    */
  def apply(startNode : Webpage, endNode : Webpage) : Weblink = new Weblink(startNode, endNode)

  /**-
    * Apply Function that takes linksource, linktarget and linklabel  as parameter
    * @param startNode webpage which is the links source
    * @param endNode webpage which ist the links target
    * @param label label of the weblink
    * @return returns a new weblink object
    */
  def apply(startNode : Webpage, endNode : Webpage, label : Weblabel) : Weblink  = {
    val weblink = new Weblink(startNode, endNode)
    for(labelentry <- label.label){
      weblink.addLabelEntry(labelentry)
    }
    weblink
  }
}

