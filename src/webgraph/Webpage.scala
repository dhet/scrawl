package webgraph

import java.net.URL


import graph.{LabelEntry, Node}

/**
  * Created by nicohein on 29/02/16.
  */
class Webpage ( val url : URL,
                var content : String = "",
                var crawled : Boolean = false) extends Node[Weblink] with PageLabel{

  /**
    *
    * @return tring containing xml description of the Webpage
    */
  def toXML() : String = {
    var xml : String = ""
    xml += s"<webpage>"
    xml += s"<url>${url.toString}</url>"
    xml += s"<crawled>$crawled</crawled>"
    xml += labelToXML()
    xml += s"</webpage>"
    xml
  }

  /**
    *
    * @return tring containing shorten xml description of the label
    */
  def toSmallXML() : String = {
    var xml = ""
    xml += s"<webpage>"
    xml += s"<url>${url.toString}</url>"
    xml += s"<edges>"
    for (edge <- edges) {
      xml += edge.toXML
    }
    xml += s"</edges>"
    xml += s"<crawled>$crawled</crawled>"
    xml += labelToXML()
    xml += s"</webpage>"
    xml
  }

  def analyze(algorithms : Seq[(Webpage) => LabelEntry]) = algorithms.foreach(alg => addLabelEntry(alg(this)))
}

object Webpage {

  def apply(url : URL) : Webpage = new Webpage(url)

  def apply(url : URL, content : String ) : Webpage = new Webpage(url, content)

  def apply(url : URL, content : String, crawled : Boolean, label : PageLabel) : Webpage = {
    var webpage = new Webpage(url, content, crawled)
    for(labelentry <- label.label){
      webpage.addLabelEntry(labelentry)
    }
    webpage
  }

}

