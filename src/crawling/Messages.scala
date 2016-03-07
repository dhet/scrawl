package crawling

import java.net.URL

import webgraph.{Webpage, Weblink, Webgraph}

object Messages {
  case class StartCrawling(url : URL)
  case class CrawlSubPage(parent : Webpage, url : URL, currentDepth : Int, visited : Set[URL])
  case class CrawlResult(weblink : Weblink)
  case class DoneCrawling()
}
