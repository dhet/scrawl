package crawling

import java.net.URL

import webgraph.Webgraph

object Messages {
  case class StartCrawling(url : URL, prefs : CrawlPrefs.type )
  case class CrawlSubPage(url : URL, prefs : CrawlPrefs.type , currentDepth : Int, visited : Set[URL])
  case class CrawlResult(graph : Webgraph)
}
