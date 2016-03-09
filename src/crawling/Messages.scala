package crawling

import java.net.URL

import akka.actor.ActorRef
import webgraph.{Webgraph, Webpage, Weblink}

/**
  * Defines Messages that can be sent in the akka system
  */
object Messages {
  case class StartCrawling(url : URL)
  case class CrawlPage(parent : Webpage, currentDepth : Int, visited : Set[URL])
  case class CrawlResult(weblink : Weblink)
  case class SendCrawlResult(link : Option[Weblink])
  case class CrawlSubPage(url : URL, parent : Webpage)
  case class DoneCrawling()
  case class InitCrawling(url : URL, master : ActorRef)
  case class DoneJob(graph : Webgraph)
}
