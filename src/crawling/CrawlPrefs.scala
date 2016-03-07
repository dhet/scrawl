package crawling

import analyze.{WordCount, AnalyzeAlgorithm}
import graph.LabelEntry
import webgraph.Webpage

object CrawlPrefs {
  var maxDepth = 2
  var threads = 3
  var analyzeFunctions = Seq[(Webpage) => LabelEntry]()
}




