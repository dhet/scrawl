package crawling

import analyze.{WordCount, AnalyzeAlgorithm}

object CrawlPrefs {
  val maxDepth = 2
  val supportedAnalyzed = List[AnalyzeAlgorithm](WordCount)
}




