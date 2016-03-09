package webgraph

import java.net.URL

import graph.Graph


/**
  * Created by nicohein on 29/02/16.
  */
class Webgraph(val root : Webpage) extends Graph[Webpage, Weblink] {

  nodes = nodes + root

  /**
    * gives the XML version of the graph
    * @return xml
    */
  def xml =
    <webgraph>
      { for (node <- nodes) yield
      {node.xml}
      }
    </webgraph>


  def sitestructure = {
    weightedDijkstra(root, (link) => (urlAnalyzer.dist(link.startNode.url, link.endNode.url)*100).asInstanceOf[Int] )
    <sitestructure>
      {root.substructure}
    </sitestructure>
  }


  /**
    * Adds a weblink to the graph
    * @param weblink weblink to be added
    * @return this
    */
  def addWeblink(weblink : Weblink) : Weblink = {
    var tempstartnode = weblink.startNode
    var tempendnode = weblink.endNode
    for(webpage <- nodes){
      if(tempstartnode.url.equals(webpage.url)){
        tempstartnode = webpage.merge(weblink.startNode)
      }
      if(tempendnode.url.equals(webpage.url)){
        tempendnode = webpage.merge(weblink.endNode)
      }
    }
    addEdge(new Weblink(tempstartnode, tempendnode))
  }

  /**
    * Removes an weblink from the graph
    * @param weblink weblink to be removed
    * @return this
    */
  def removeWeblink(weblink: Weblink) : Graph[Webpage, Weblink] = {
    for(edge <- edges){
      if(weblink.startNode.url.equals(edge.startNode.url) && weblink.endNode.url.equals(edge.endNode.url))
        removeEdge(edge)
    }
    this
  }

  /**
    * Counts the number of uncrawled pages / is equal to links deaper than specified crawl level
    * @return number of nodes not crawled yet
    */
  def countUncrawledNodes() : Int = {
    var count : Int  = 0
    for(node <- nodes){
      if(!node.crawled)
        count += 1
    }
    count
  }

  object urlAnalyzer{
    def dist(url1: URL, url2: URL) : Double = 1/ sim(url1, url2)

    def sim(url1: URL, url2: URL) : Double = {
      //http://www2007.org/workshops/paper_103.pdf
      if(url1.toString.length >0 || url2.toString.length >0)
        return set(url1.toString, 4).intersect(set(url2.toString, 4)).size.asInstanceOf[Double] / set(url1.toString, 4).union(set(url2.toString, 4)).size.asInstanceOf[Double]
      throw new Exception
    }

    private def set(s: String, r : Int) : Set[String] = {
      var substring = Set[String]()
      for(i <- 1 to s.length-r){
        substring += s.substring(i, i+r)
      }
      substring
    }

  }
}


object Webgraph {
  def apply(root: Webpage) = new Webgraph(root)
}
