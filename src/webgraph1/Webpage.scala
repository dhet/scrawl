package webgraph1

import java.net.URL

import abstractGraph.AbstractNode

/**
  * Created by nicohein on 29/02/16.
  */
class Webpage ( val url : URL,
                var content : String = "",
                var crawled : Boolean = false,
                override val label : PageLabel = PageLabel()) extends AbstractNode[Weblink]() {


  /**
    *
    * @return tring containing xml description of the Webpage
    */
  def toXML() : String = {
    var xml : String = ""
    xml += s"<webpage>"
    xml += s"<url>${url.toString}</url>"
    xml += s"<content>$content</content>"
    xml += s"<edges>"
    for(edge <- edges){
      xml += edges.head.toXML()
    }
    xml += s"</edges>"
    xml += s"<crawled>$crawled</crawled>"
    xml += s"<visited>$visited</visited>"
    xml += label.toXML
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
    xml += label.toXML()
    xml += s"</webpage>"
    xml
  }
}

object Webpage {

  def apply(url : URL) : Webpage = new Webpage(url) //for root

  def apply(url : URL, content : String ) : Webpage = new Webpage(url, content)//while crawling

  def apply(url : URL, edges : List[Weblink]) : Webpage = {
    var webpage = new Webpage(url)
    for(e<-edges){
      webpage.addEdge(e)
    }
    webpage
  }

  def apply(url : URL, content : String , edges : List[Weblink]) : Webpage = {
    var webpage = new Webpage(url, content)
    for(e<-edges){
      webpage.addEdge(e)
    }
    webpage
  }

  def apply(url : URL, content : String, edges : List[Weblink], crawled : Boolean, label : PageLabel) : Webpage = {
    var webpage = new Webpage(url, content, crawled, label)
    for(e<-edges){
      webpage.addEdge(e)
    }
    webpage
  }

}
