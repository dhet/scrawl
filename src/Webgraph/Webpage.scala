package Webgraph

import AbstractGraph.AbstractNode

/**
  * Created by nicohein on 29/02/16.
  */
class Webpage ( val url : String,
                var content : String = "",
                subedges :  List[Weblink] = Nil,
                var crawled : Boolean = false,
                subvisited : Boolean = false,
                sublevel : Int = 0,
                sublabel : PageLabel = PageLabel()) extends AbstractNode[Weblink](subedges, subvisited, sublevel, sublabel) {

  def toXML() : String = {
    var xml : String = ""
    xml += s"<webpage>"
    xml += s"<url>$url</url>"
    xml += s"<content>$content</content>"
    xml += s"<edges>"
    for(edge <- edges){
      xml += edges.head.toXML()
    }
    xml += s"</edges>"
    xml += s"<crawled>$crawled</crawled>"
    xml += s"<visited>$visited</visited>"
    xml += s"<level>$level</level>"
    xml += label.toXML()
    xml += s"</webpage>"
    xml
  }
  def toSmallXML() : String = {
    var xml: String = ""
    xml += s"<webpage>"
    xml += s"<url>$url</url>"
    xml += s"<edges>"
    for (edge <- edges) {
      xml += edge.toXML()
    }
    xml += s"</edges>"
    xml += s"<crawled>$crawled</crawled>"
    xml += s"<level>$level</level>"
    xml += label.toXML()
    xml += s"</webpage>"
    xml
  }
}

object Webpage {
  def apply(url : String) : Webpage = new Webpage(url) //for root
  def apply(url : String, content : String, edges : List[Weblink]): Webpage = new Webpage(url, content, edges)//while crawling
  def apply(url : String, content : String, edges : List[Weblink], crawled : Boolean, visited : Boolean, level : Int, label : PageLabel) : Webpage = new Webpage(url, content, edges, crawled, visited, level, label)//full constructor

}

