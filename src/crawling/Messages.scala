package crawling

import java.net.URL

import webgraph1.Webgraph

object Messages {
  case class CrawlPage(url : URL, prefs : CrawlPrefs.type )
  case class CrawlSubPage(url : URL, prefs : CrawlPrefs.type , currentDepth : Int, visited : Set[URL])
  case class CrawlResult(graph : Webgraph)
}
