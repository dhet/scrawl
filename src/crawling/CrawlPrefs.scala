package crawling

import analyze.{WordCount, AnalyzeAlgorithm}

object CrawlPrefs {
  var maxDepth = 2
  var supportedAnalyzed = List[AnalyzeAlgorithm](WordCount)
}




