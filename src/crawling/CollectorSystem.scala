package crawling

import java.net.URL
import java.util.concurrent.TimeUnit

import akka.actor.{Props, Actor, ActorSystem}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import crawling.Messages._
import webgraph.{Webpage, Webgraph}
import akka.pattern.ask

import scala.concurrent.Await

/**
  * An acca system to collect weblinks and assemble a graph from the links. Provides a high level crawling function.
  */
object CollectorSystem{
  val collectorSystem = ActorSystem("collectorsystem", ConfigFactory.load.getConfig("collectorsystem"))
  val crawlSystem = ActorSystem("crawlsystem", ConfigFactory.load.getConfig("crawlsystem"))

  /**
    * Central gateway to the crawler system. Bootstraps the whole crawler system to crawl a page and returns a graph
    * representing the site structure. Blocks until the page was fully crawled.
    * @param url  The root page to crawl
    * @return     The graph generated from the crawler input
    */
  def crawlPage(url : URL) = {
    val graph = Webgraph(Webpage(url))
    val safeUrlName = url.toString.replaceAll("[^w]", "~")
    val collector = collectorSystem.actorOf(Props(classOf[CollectorActor], graph), "collector" + safeUrlName)
    val crawlerMaster = crawlSystem.actorOf(Props(classOf[CrawlerSystem.CrawlerMaster], collector), "crawler" + safeUrlName)

    implicit val timeout = Timeout(20, TimeUnit.MINUTES)
    val future = crawlerMaster ? StartCrawling(url)
    // wait for result
    Await.result(future, timeout.duration)
    graph
  }

  /**
    * Central actor that receives messages from crawler workers.
    * @param graph  The graph to assemble. Will be modified continuously during the crawling process
    */
  class CollectorActor(graph : Webgraph) extends Actor{
    def receive = {
      case CrawlResult(link) => {
        println(s"Added Link $link")
        graph.addWeblink(link)
      }
    }
  }
}
