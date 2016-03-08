package crawling

import graph.LabelEntry
import webgraph.{Weblink, Webpage}

object CrawlPrefs {
  var maxDepth = 2
  var threads = 3
  var analyzeFunctionsPages = Seq[(Webpage) => LabelEntry]()
  var analyzeFunctionsLinks = Seq[(Weblink) => LabelEntry]()

  def addPageAnalyzeFunction(alg : (Webpage) => LabelEntry) = {
    analyzeFunctionsPages = analyzeFunctionsPages :+ alg
  }

}




