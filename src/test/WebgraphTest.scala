package test

import java.net.URL

import abstractGraph.{LabelEntry, Label}
import webgraph1._
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable

/**
  * Created by nicohein on 02/03/16.
  * The Webgraph test includes all tests for the abstract graph since abstract clases could ouly be tested indirectly
  */
class WebgraphTest extends FlatSpec with Matchers{

  "A Label" should "be a mutable ListMap" in {
    val labellistmap : mutable.ListMap[String, Any] = Label
    val linklabellistmap : mutable.ListMap[String, Any] = LinkLabel()
    val pagelabellistmap : mutable.ListMap[String, Any] = PageLabel()

    labellistmap should be (Label)
    linklabellistmap should be (LinkLabel())
    pagelabellistmap should be (PageLabel())
  }

  "A Label" should "provide xml" in {

    val linklabellistmap : LinkLabel = LinkLabel()
    val pagelabellistmap : PageLabel = PageLabel()


    linklabellistmap.+=(("Key", "Value"))
    pagelabellistmap.+=(("Key2", "Value"))
    Thread sleep 10
    pagelabellistmap.+=(("Key1", "Value"))


    linklabellistmap.toXML() should be ("<linklabel><key>Key</key><value>Value</value></linklabel>")
    pagelabellistmap.toXML() should be ("<pagelabel><key>Key1</key><value>Value</value><key>Key2</key><value>Value</value></pagelabel>")
  }

  "A Label" should "provide a List of Values" in {
    val labellistmap : Label = Label

    labellistmap.+=(("Key2", "Value2"))
    Thread sleep 10
    labellistmap.+=(("Key1", "Value1"))

    labellistmap.toString() should be ("Map(Key1 -> Value1, Key2 -> Value2)")

  }

  "A Webpage" should "provide labeling options" in {
    val webpage = Webpage(new URL("http://url"))

    webpage.addLabelEntry(new LabelEntry("key", "value"))
    webpage.getLabelEntry("key") should be ("value")

    webpage.updateLabelEntry(new LabelEntry("key", "updated"))
    webpage.getLabelEntry("key") should be ("updated")

    webpage.toXML() should be ("<webpage><url>http://url</url><content></content><edges></edges><crawled>false</crawled><visited>false</visited><pagelabel><key>key</key><value>updated</value></pagelabel></webpage>")

    webpage.removeLabelEntry("key")
    webpage.toXML() should be ("<webpage><url>http://url</url><content></content><edges></edges><crawled>false</crawled><visited>false</visited></webpage>")

  }
  "A Weblink" should "provide labeling options" in {
    var webpage1 = Webpage(new URL("http://url1"))
    var webpage2 = Webpage(new URL("http://url2"))
    var weblink = Weblink(webpage1, webpage2)

    weblink.addLabelEntry(new LabelEntry("key", "value"))
    weblink.getLabelEntry("key") should be ("value")

    weblink.updateLabelEntry(new LabelEntry("key", "updated"))
    weblink.getLabelEntry("key") should be ("updated")

    weblink.toXML() should be ("<weblink><startnode>http://url1</startnode><endnode>http://url2</ednode><linklabel><key>key</key><value>updated</value></linklabel></weblink>")

    weblink.removeLabelEntry("key")
    weblink.toXML() should be ("<weblink><startnode>http://url1</startnode><endnode>http://url2</ednode></weblink>")

  }

  "A Webpage" should "handle edges" in {
    var webpage1 = Webpage(new URL("http://url1"))
    var webpage2 = Webpage(new URL("http://url2"))
    var weblink = Weblink(webpage1, webpage2)
    weblink.toXML() should be ("<weblink><startnode>http://url1</startnode><endnode>http://url2</ednode></weblink>")

    webpage1.addEdge(weblink)
    webpage2.addEdge(weblink)
    webpage1.toXML() should be ("<webpage><url>http://url1</url><content></content><edges><weblink><startnode>http://url1</startnode><endnode>http://url2</ednode></weblink></edges><crawled>false</crawled><visited>false</visited></webpage>")
    webpage2.toXML() should be ("<webpage><url>http://url2</url><content></content><edges><weblink><startnode>http://url1</startnode><endnode>http://url2</ednode></weblink></edges><crawled>false</crawled><visited>false</visited></webpage>")

    webpage1.removeEdge(weblink)
    webpage1.toXML() should be ("<webpage><url>http://url1</url><content></content><edges></edges><crawled>false</crawled><visited>false</visited></webpage>")

  }



  "A Webgraph" should "be conected" in {
    //nodes (Webpages) of graph
    var root = Webpage(new URL("http://root.com"))
    var rootsub1 = Webpage(new URL("http://root.com/sub1"))
    var rootsub2 = Webpage(new URL("http://root.com/sub2"))
    var rootsub1sub1 = Webpage(new URL("http://root.com/sub1/sub1"))
    var rootsub2sub1 = Webpage(new URL("http://root.com/sub2/sub1"))

    var offpage1 = Webpage(new URL("http://offpage1.com"))
    var offpage2 = Webpage(new URL("http://offpage2.com"))

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

    webgraph.countNodes() should be (7)
    webgraph.countUncrawledNodes() should be (7)

    webgraph.nextUncrawledNode().url.toString should be ("http://root.com")
    root.crawled = true
    webgraph.nextUncrawledNode().url.toString should be ("http://root.com/sub1")
    rootsub1.crawled = true
    webgraph.nextUncrawledNode().url.toString should be ("http://root.com/sub2")
    rootsub2.crawled = true
    webgraph.nextUncrawledNode().url.toString should be ("http://root.com/sub1/sub1")
    rootsub1sub1.crawled = true
    webgraph.nextUncrawledNode().url.toString should be ("http://root.com/sub2/sub1")
    rootsub2sub1.crawled = true
    webgraph.nextUncrawledNode().url.toString should be ("http://offpage2.com")
    offpage2.crawled = true
    webgraph.nextUncrawledNode().url.toString should be ("http://offpage1.com")
    offpage1.crawled = true


    webgraph.countUncrawledNodes() should be (0)
    webgraph.countNodes() should be (7)

    root.edges.map((e) => e.endNode.url) should be (Set(rootsub1.url, rootsub2.url))
    webgraph.generateSitemap() should be (List("http://root.com", "http://root.com/sub1", "http://root.com/sub2", "http://root.com/sub2/sub1", "http://offpage2.com", "http://root.com/sub1/sub1", "http://offpage1.com"))
    webgraph.analyzeLinktypes()

    edge1.getLabelEntry("linktype").asInstanceOf[Inlink].toString should be ("Inlink(http://root.com/sub1)")
    edge2.getLabelEntry("linktype").asInstanceOf[Inlink].toString should be ("Inlink(http://root.com/sub2)")
    edge6.getLabelEntry("linktype").asInstanceOf[Outlink].toString should be ("Outlink(http://offpage1.com)")



    //webgraph.edges.toList.map((edge : Weblink) => edge.getLabelEntry("linktype")) should be (9)

    //webgraph.contraintBreadthFirstTraversal(root, (weblink: Weblink) => if(weblink.getLabelEntry("linktype").isInstanceOf[Inlink]) true else false, (webpage: Webpage) => true )/*.map((webpage: Webpage) => webpage.url)*/ should be (1)
    //webgraph.dijkstra(root).breadthFirstTraversal(root).map((page) => (page.getLabelEntry("dijkstra"), page.url)) should be (1)
    //webgraph.constraintDijkstra(root, (edge) => edge.getLabelEntry("linktype").isInstanceOf[Inlink], (node) => true).breadthFirstTraversal(root).map((page) => (page.getLabelEntry("dijkstra"), page.url)) should be (1)

  }

}
