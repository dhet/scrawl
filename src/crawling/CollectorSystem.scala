package crawling

import java.net.URL
import java.util.concurrent.TimeUnit

import akka.actor.{Props, Actor, ActorRef, ActorSystem}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import crawling.CrawlerSystem.CrawlerWorker
import crawling.Messages.{DoneCrawling, CrawlResult, StartCrawling}
import webgraph.{Webpage, Webgraph, Weblink}
import akka.pattern.ask

import scala.concurrent.Await

object CollectorSystem{

  def crawlPage(url : URL) = {
    val collectorSystem = ActorSystem("collectorsystem", ConfigFactory.load.getConfig("collectorsystem"))
    val crawlSystem = ActorSystem("crawlsystem", ConfigFactory.load.getConfig("crawlsystem"))
    val graph = Webgraph(Webpage(url))
    val collector = collectorSystem.actorOf(Props(classOf[CollectorActor], graph), "collector")
    val crawlerRoot = crawlSystem.actorOf(Props(classOf[CrawlerSystem.CrawlerWorker], collector), "crawler")

//    implicit val timeout = Timeout(5, TimeUnit.MINUTES)
    val future = crawlerRoot ! StartCrawling(url)
//    val result = Await.result(future, timeout.duration)
//    mainActor ! StartCrawling(url)
  }

  class CollectorActor(graph : Webgraph) extends Actor{
    def receive = {
      case CrawlResult(link) => {
        println(s"added link ${link.startNode.url} -> ${link.endNode.url}")
        graph.addWeblink(link)
      }
      case DoneCrawling => println("done!")
    }
  }
}
