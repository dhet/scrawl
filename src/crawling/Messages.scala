package crawling

import java.net.URL

import webgraph.{Webpage, Weblink}

object Messages {
  case class StartCrawling(url : URL)
  case class CrawlPage(parent : Webpage, url : URL, currentDepth : Int, visited : Set[URL])
  case class CrawlResult(weblink : Weblink)
  case class Visited(urls : Set[URL])
  case class AddToVisited(urls : Set[URL])
  case class BeginThread()
  case class EndThread()
  case class SendCrawlResult(link : Option[Weblink])
  case class CrawlSubPage(url : URL, parent : Webpage)
  case class DoneCrawling()
}
