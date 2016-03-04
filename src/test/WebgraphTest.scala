package test

import AbstractGraph.Label
import Webgraph._
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable

/**
  * Created by nicohein on 02/03/16.
  * The Webgraph test includes all tests for the abstract graph since abstract clases could ouly be tested indirectly
  */
class WebgraphTest extends FlatSpec with Matchers{

  "A Label" should "be a mutable ListMap" in {
    val labellistmap : mutable.ListMap[String, Any] = Label()
    val linklabellistmap : mutable.ListMap[String, Any] = LinkLabel()
    val pagelabellistmap : mutable.ListMap[String, Any] = PageLabel()

    labellistmap should be (Label())
    linklabellistmap should be (LinkLabel())
    pagelabellistmap should be (PageLabel())
  }

  "A Label" should "provide xml" in {
    val labellistmap : Label = Label()
    val linklabellistmap : Label = LinkLabel()
    val pagelabellistmap : Label = PageLabel()

    labellistmap.+=(("Key", "Value"))
    linklabellistmap.+=(("Key", "Value"))
    pagelabellistmap.+=(("Key2", "Value"))
    Thread sleep 10
    pagelabellistmap.+=(("Key1", "Value"))

    labellistmap.toXML() should be ("<label><key>Key</key><value>Value</value></label>")
    linklabellistmap.toXML() should be ("<linklabel><key>Key</key><value>Value</value></linklabel>")
    pagelabellistmap.toXML() should be ("<pagelabel><key>Key1</key><value>Value</value><key>Key2</key><value>Value</value></pagelabel>")
  }

  "A Label" should "provide a List of Values" in {
    val labellistmap : Label = Label()

    labellistmap.+=(("Key2", "Value2"))
    Thread sleep 10
    labellistmap.+=(("Key1", "Value1"))

    labellistmap.toString() should be ("Map(Key1 -> Value1, Key2 -> Value2)")

  }

  "A Webpage" should "provide labeling options" in {
    var webpage = Webpage("url")

    webpage.addLabelEntry("key", "value")
    webpage.getLabelEntry("key") should be ("value")

    webpage.updateLabelEntry("key", "updated")
    webpage.getLabelEntry("key") should be ("updated")

    webpage.toXML() should be ("<webpage><url>url</url><content></content><edges></edges><crawled>false</crawled><visited>false</visited><level>0</level><pagelabel><key>key</key><value>updated</value></pagelabel></webpage>")

    webpage.removeLabelEntry("key")
    webpage.toXML() should be ("<webpage><url>url</url><content></content><edges></edges><crawled>false</crawled><visited>false</visited><level>0</level><pagelabel></pagelabel></webpage>")

  }
  "A Weblink" should "provide labeling options" in {
    var webpage1 = Webpage("url1")
    var webpage2 = Webpage("url2")
    var weblink = Weblink(webpage1, webpage2)

    weblink.addLabelEntry("key", "value")
    weblink.getLabelEntry("key") should be ("value")

    weblink.updateLabelEntry("key", "updated")
    weblink.getLabelEntry("key") should be ("updated")

    weblink.toXML() should be ("<weblink><startnode>url1</startnode><endnode>url2</ednode><linklabel><key>key</key><value>updated</value></linklabel></weblink>")

    weblink.removeLabelEntry("key")
    weblink.toXML() should be ("<weblink><startnode>url1</startnode><endnode>url2</ednode><linklabel></linklabel></weblink>")

  }

  "A Webpage" should "handle edges" in {
    var webpage1 = Webpage("url1")
    var webpage2 = Webpage("url2")
    var weblink = Weblink(webpage1, webpage2)
    weblink.toXML() should be ("<weblink><startnode>url1</startnode><endnode>url2</ednode><linklabel></linklabel></weblink>")

    webpage1.addEdge(weblink)
    webpage2.addEdge(weblink)
    webpage1.toXML() should be ("<webpage><url>url1</url><content></content><edges><weblink><startnode>url1</startnode><endnode>url2</ednode><linklabel></linklabel></weblink></edges><crawled>false</crawled><visited>false</visited><level>0</level><pagelabel></pagelabel></webpage>")
    webpage2.toXML() should be ("<webpage><url>url2</url><content></content><edges><weblink><startnode>url1</startnode><endnode>url2</ednode><linklabel></linklabel></weblink></edges><crawled>false</crawled><visited>false</visited><level>0</level><pagelabel></pagelabel></webpage>")

    webpage1.removeEdge(weblink)
    webpage1.toXML() should be ("<webpage><url>url1</url><content></content><edges></edges><crawled>false</crawled><visited>false</visited><level>0</level><pagelabel></pagelabel></webpage>")

  }



  "A Webgraph" should "be coneected" in {
    //nodes (Webpages) of graph
    var root = Webpage("http://root.com")
    var rootsub1 = Webpage("http://root.com/sub1")
    var rootsub2 = Webpage("http://root.com/sub2")
    var rootsub1sub1 = Webpage("http://root.com/sub1/sub1")
    var rootsub2sub1 = Webpage("http://root.com/sub2/sub1")

    var offpage1 = Webpage("http://offpage1.com")
    var offpage2 = Webpage("http://offpage2.com")

    //edges (Weblinks) of graph
    var edge1 = Weblink(root, rootsub1)
    var edge2 = Weblink(root, rootsub2)
    var edge3 = Weblink(rootsub1, rootsub2)
    var edge4 = Weblink(rootsub1, rootsub1sub1)
    var edge5 = Weblink(rootsub1, rootsub2sub1)
    var edge6 = Weblink(rootsub1sub1, offpage1)
    var edge7 = Weblink(rootsub1sub1, rootsub2sub1)
    var edge8 = Weblink(rootsub2, rootsub2sub1)
    var edge9 = Weblink(rootsub2, offpage2)


    val webgraph : Webgraph = Webgraph(root)

    //to ensure the webgraph is fully connected is is constructed only with edges except the root
    webgraph.addWeblink(edge1)
    webgraph.addWeblink(edge2)
    webgraph.addWeblink(edge3)
    webgraph.addWeblink(edge4)
    webgraph.addWeblink(edge5)
    webgraph.addWeblink(edge6)
    webgraph.addWeblink(edge7)
    webgraph.addWeblink(edge8)
    webgraph.addWeblink(edge9)

    webgraph.count() should be (7)
    webgraph.countUncrawled() should be (7)

    webgraph.nextUncrawledNode().url should be ("http://root.com")
    root.crawled = true
    webgraph.nextUncrawledNode().url should be ("http://root.com/sub2")
    rootsub2.crawled = true
    webgraph.nextUncrawledNode().url should be ("http://root.com/sub1")
    rootsub1.crawled = true
    webgraph.nextUncrawledNode().url should be ("http://offpage2.com")
    offpage2.crawled = true
    webgraph.nextUncrawledNode().url should be ("http://root.com/sub2/sub1")
    rootsub2sub1.crawled = true
    webgraph.nextUncrawledNode().url should be ("http://root.com/sub1/sub1")
    rootsub1sub1.crawled = true
    webgraph.nextUncrawledNode().url should be ("http://offpage1.com")
    offpage1.crawled = true

    webgraph.countUncrawled() should be (0)
    webgraph.count() should be (7)

    root.edges.map((e) => e.endNode.url) should be (List(rootsub2.url, rootsub1.url))
    webgraph.generateSitemap() should be (List("http://root.com", "http://root.com/sub1", "http://root.com/sub2", "http://root.com/sub2/sub1", "http://offpage2.com", "http://root.com/sub1/sub1", "http://offpage1.com"))

    edge1.getLabelEntry("linktype") should be ("inlink")
    edge2.getLabelEntry("linktype") should be ("inlink")
    edge6.getLabelEntry("linktype") should be ("outlink")

    //webgraph.contraintBreadthFirstTraversal(root, (weblink: Weblink) => if(weblink.getLabelEntry("linktype") == "inlink") true else false, (webpage: Webpage) => true ).map((webpage: Webpage) => webpage.url) should be (1)

  }

}
