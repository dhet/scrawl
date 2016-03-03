package test

import Webgraph.{Webgraph, Webpage, PageLabel}
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by nicohein on 02/03/16.
  */
class WebgraphTest extends FlatSpec with Matchers{


  "A Webgraph" should "be instaciable" in {
    val webgraph : Webgraph = Webgraph(new Webpage("url1", "content1", new PageLabel()))

    webgraph.countUncrawled() should be (1)

  }

}
