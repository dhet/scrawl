package crawling

import analyze.{WordCount, AnalyzeAlgorithm}

object CrawlPrefs {
  var maxDepth = 2
  var threads = 3
  var supportedAnalyzed = List[AnalyzeAlgorithm](WordCount)
}




