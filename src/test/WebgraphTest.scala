package test

import java.net.URL

import graph.LabelEntry
import org.scalatest.{FlatSpec, Matchers}
import webgraph._

import scala.xml.PrettyPrinter

/**
  * Created by nicohein on 02/03/16.
  * The Webgraph test includes all tests for the abstract graph since abstract clases could ouly be tested indirectly
  */
class WebgraphTest extends FlatSpec with Matchers{

  /**
    * The Label is tested implicitly
    */

  "A Webpage" should "provide labeling options" in {
    val webpage = Webpage(new URL("http://url"))

    webpage.addLabelEntry(new LabelEntry("key", "value"))
    webpage.getLabelEntry("key").get should be ("value")

    webpage.updateLabelEntry(new LabelEntry("key", "updated"))
    webpage.getLabelEntry("key").get should be ("updated")

    //for remove see test "A webpage should provide xml"

  }

  "A Webpage" should "provide xml" in {
    val webpage = Webpage(new URL("http://url"))
    webpage.addLabelEntry(new LabelEntry("key", "value"))

    var pp = new PrettyPrinter(80, 2)
    pp.format(webpage.xml) should be ("<webpage url=\"http://url\" crawled=\"false\">\n  <labels>\n    <label key=\"key\" value=\"value\"/>\n  </labels>\n  <links> </links>\n</webpage>")

    webpage.removeLabelEntry("key")
    pp.format(webpage.xml) should be ("<webpage url=\"http://url\" crawled=\"false\">\n  <labels> </labels>\n  <links> </links>\n</webpage>")

  }

  "A Weblink" should "provide labeling options" in {
    var webpage1 = Webpage(new URL("http://url1"))
    var webpage2 = Webpage(new URL("http://url2"))
    var weblink = Weblink(webpage1, webpage2)

    weblink.addLabelEntry(new LabelEntry("key", "value"))
    weblink.getLabelEntry("key").get should be ("value")

    weblink.updateLabelEntry(new LabelEntry("key", "updated"))
    weblink.getLabelEntry("key").get should be ("updated")

    //for remove see test "A weblink should provide xml"
  }

  "A weblink" should "ba analyzable" in {
    var webpage1 = Webpage(new URL("http://url1"))
    var webpage2 = Webpage(new URL("http://url2"))
    var weblink = Weblink(webpage1, webpage2)

    weblink.analyze(Seq((w : Weblink) => new LabelEntry("test" , "1")))
    weblink.getLabelEntry("test").get should be ("1")
  }

  "A weblink" should "provide xml" in {
    var webpage1 = Webpage(new URL("http://url1"))
    var webpage2 = Webpage(new URL("http://url2"))
    var weblink = Weblink(webpage1, webpage2)

    var pp = new PrettyPrinter(300, 2)
    pp.format(weblink.xml) should be ("<weblink source=\"http://url1\" target=\"http://url2\">\n  <labels> </labels>\n</weblink>")

    weblink.addLabelEntry(new LabelEntry("key", "value"))
    pp.format(weblink.xml) should be ("<weblink source=\"http://url1\" target=\"http://url2\">\n  <labels>\n    <label key=\"key\" value=\"value\"/>\n  </labels>\n</weblink>")
  }


  "A Graph" should "count its elements" in {
    var root = Webpage(new URL("http://root.com"))

    val webgraph : Webgraph = Webgraph(root)
    webgraph.countNodes() should be (1)
    webgraph.countEdges() should be (0)

    //adding an Weblink
    var rootsub1 = Webpage(new URL("http://root.com/sub1"))
    var edge1 = Weblink(root, rootsub1)
    webgraph.addWeblink(edge1)
    webgraph.countNodes() should be (2)
    webgraph.countEdges() should be (1)


  }

  "A Graph" should "should handle remove and add operations" in {
    var root = Webpage(new URL("http://root.com"))

    val webgraph : Webgraph = Webgraph(root)

    //adding an Weblink is implicit for adding two nodes and one edge
    var rootsub1 = Webpage(new URL("http://root.com/sub1"))
    var rootsub2 = Webpage(new URL("http://root.com/sub2"))

    var edge1 = Weblink(root, rootsub1)
    var edge2 = Weblink(root, rootsub2)
    var edge3 = Weblink(rootsub1, rootsub2)
    webgraph.addWeblink(edge1)
    webgraph.addWeblink(edge2)
    webgraph.addWeblink(edge3)

    var pp = new PrettyPrinter(80, 2)
    //adding the same link twice should not change anything
    webgraph.addWeblink(edge1)
    webgraph.addWeblink(edge1)
    //webgraph.nodes.map((node) => node.url ).toString() should be (1)
    //webgraph.edges.toString should be (1)
    pp.format(webgraph.xml) should be ("<webgraph>\n  <webpage url=\"http://root.com\" crawled=\"false\">\n    <labels> </labels>\n    <links>\n      <link url=\"http://root.com/sub1\"/>\n      <link url=\"http://root.com/sub2\"/>\n    </links>\n  </webpage>\n  <webpage url=\"http://root.com/sub1\" crawled=\"false\">\n    <labels> </labels>\n    <links>\n      <link url=\"http://root.com/sub2\"/>\n    </links>\n  </webpage>\n  <webpage url=\"http://root.com/sub2\" crawled=\"false\">\n    <labels> </labels>\n    <links> </links>\n  </webpage>\n</webgraph>")

    webgraph.countNodes() should be (3)
    webgraph.countEdges() should be (3)

    //removing edge (root, rootsub2) should only delete one edge
    webgraph.removeWeblink(edge2)
    webgraph.countNodes() should be (3)
    webgraph.countEdges() should be (2)

    //removing edge (root, rootsub1) however should also delete node sub1 since there isn't any link to it and the graph is defined as connected
    webgraph.addWeblink(edge2)
    webgraph.removeWeblink(edge1)
    webgraph.countNodes() should be (2)
    webgraph.countEdges() should be (1)


    pp.format(webgraph.xml) should be ("<webgraph>\n  <webpage url=\"http://root.com\" crawled=\"false\">\n    <labels> </labels>\n    <links>\n      <link url=\"http://root.com/sub2\"/>\n    </links>\n  </webpage>\n  <webpage url=\"http://root.com/sub2\" crawled=\"false\">\n    <labels> </labels>\n    <links> </links>\n  </webpage>\n</webgraph>")
  }


  "A Webgraph" should "be able to analyze its nodes and edges" in {
    var root = Webpage(new URL("http://root.com"))

    val webgraph: Webgraph = Webgraph(root)

    //adding an Weblink is implicit for adding two nodes and one edge
    var rootsub1 = Webpage(new URL("http://root.com/sub1"))
    var rootsub2 = Webpage(new URL("http://root.com/sub2"))

    var edge1 = Weblink(root, rootsub1)
    var edge2 = Weblink(root, rootsub2)
    var edge3 = Weblink(rootsub1, rootsub2)
    edge1 = webgraph.addWeblink(edge1)
    edge2 = webgraph.addWeblink(edge2)
    edge3 = webgraph.addWeblink(edge3)

    webgraph.analyzeEdges((link) => new LabelEntry("graphanalyze", 1))
    edge1.getLabelEntry("graphanalyze").get should be (1)

    webgraph.analyzeNodes((page) => new LabelEntry("graphanalyze", 2))
    root.getLabelEntry("graphanalyze").get should be (2)

  }

  "A Wabgraph" should "be traversable and should perform dijkstra" in {

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


    val webgraph: Webgraph = Webgraph(root)
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

    webgraph.breadthFirstTraversal(root).toString() should be("List(Webpage(url:http://root.com, Set(Edge(startnode:http://root.com, endnode:http://root.com/sub1 ), Edge(startnode:http://root.com, endnode:http://root.com/sub2 ))), Webpage(url:http://root.com/sub1, Set(Edge(startnode:http://root.com/sub1, endnode:http://root.com/sub2 ), Edge(startnode:http://root.com/sub1, endnode:http://root.com/sub1/sub1 ), Edge(startnode:http://root.com/sub1, endnode:http://root.com/sub2/sub1 ))), Webpage(url:http://root.com/sub2, Set(Edge(startnode:http://root.com/sub2, endnode:http://root.com/sub2/sub1 ), Edge(startnode:http://root.com/sub2, endnode:http://offpage2.com ))), Webpage(url:http://root.com/sub1/sub1, Set(Edge(startnode:http://root.com/sub1/sub1, endnode:http://offpage1.com ), Edge(startnode:http://root.com/sub1/sub1, endnode:http://root.com/sub2/sub1 ))), Webpage(url:http://root.com/sub2/sub1, Set()), Webpage(url:http://offpage2.com, Set()), Webpage(url:http://offpage1.com, Set()))")
    webgraph.depthFirstTraversal(root).toString() should be("List(Webpage(url:http://root.com, Set(Edge(startnode:http://root.com, endnode:http://root.com/sub1 ), Edge(startnode:http://root.com, endnode:http://root.com/sub2 ))), Webpage(url:http://root.com/sub1, Set(Edge(startnode:http://root.com/sub1, endnode:http://root.com/sub2 ), Edge(startnode:http://root.com/sub1, endnode:http://root.com/sub1/sub1 ), Edge(startnode:http://root.com/sub1, endnode:http://root.com/sub2/sub1 ))), Webpage(url:http://root.com/sub2, Set(Edge(startnode:http://root.com/sub2, endnode:http://root.com/sub2/sub1 ), Edge(startnode:http://root.com/sub2, endnode:http://offpage2.com ))), Webpage(url:http://root.com/sub2/sub1, Set()), Webpage(url:http://offpage2.com, Set()), Webpage(url:http://root.com/sub1/sub1, Set(Edge(startnode:http://root.com/sub1/sub1, endnode:http://offpage1.com ), Edge(startnode:http://root.com/sub1/sub1, endnode:http://root.com/sub2/sub1 ))), Webpage(url:http://offpage1.com, Set()))")


    webgraph.analyzeEdges((weblink) => new LabelEntry("linktype", if(weblink.endNode.url.getHost.equals(weblink.startNode.url.getHost)) "inlink" else "outlink"))
    webgraph.analyzeNodes((webpage) => new LabelEntry("linktype", if(webpage.url.getHost.equals(root.url.getHost)) "inpage" else "outpage"))

    webgraph.contraintBreadthFirstTraversal(root, (weblink: Weblink) => if(weblink.getLabelEntry("linktype").get.equals("inlink")) true else false, (webpage: Webpage) => true ).map((webpage: Webpage) => webpage.url.toString).toString() should be ("List(http://root.com, http://root.com/sub1, http://root.com/sub2, http://root.com/sub1/sub1, http://root.com/sub2/sub1)")
    webgraph.contraintBreadthFirstTraversal(root, (weblink: Weblink) => true, (webpage: Webpage) => if(webpage.getLabelEntry("linktype").get.equals("inpage")) true else false ).map((webpage: Webpage) => webpage.url.toString).toString() should be ("List(http://root.com, http://root.com/sub1, http://root.com/sub2, http://root.com/sub1/sub1, http://root.com/sub2/sub1)")


    webgraph.dijkstra(root).breadthFirstTraversal(root).map((page) => (page.getLabelEntry("dijkstra").get, page.url.toString)).toString should be ("List((0,http://root.com), (1,http://root.com/sub1), (1,http://root.com/sub2), (2,http://root.com/sub1/sub1), (2,http://root.com/sub2/sub1), (2,http://offpage2.com), (3,http://offpage1.com))")
    webgraph.weightedDijkstra(root, (link) => 2).breadthFirstTraversal(root).map((page) => (page.getLabelEntry("dijkstra").get, page.url.toString)).toString should be ("List((0,http://root.com), (2,http://root.com/sub1), (2,http://root.com/sub2), (4,http://root.com/sub1/sub1), (4,http://root.com/sub2/sub1), (4,http://offpage2.com), (6,http://offpage1.com))")

    var pp = new PrettyPrinter(80, 2)
    pp.format(webgraph.sitestructure) should be (1)
  }







}
