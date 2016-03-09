package crawling

import java.net.URL

import webgraph.{Webpage, Weblink}

/**
  * Defines Messages that can be sent in the akka system
  */
object Messages {
  case class StartCrawling(url : URL)
  case class CrawlWebsite(parent : Webpage, currentDepth : Int, visited : Set[URL])
  case class CrawlSinglePage(url : URL, parent : Webpage)
  case class PageCrawlResult(link : Option[Weblink])
  case class LinkResult(weblink : Weblink)
  case class DoneCrawling(visitedCount : Int)
}
