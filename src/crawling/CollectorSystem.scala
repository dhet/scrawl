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
    val mainSystem = ActorSystem("crawling", ConfigFactory.load.getConfig("mainsystem"))
    val crawlSystem = ActorSystem("crawlSystem", ConfigFactory.load.getConfig("crawlsystem"))
    val graph = Webgraph(Webpage(url))
    val collector = mainSystem.actorOf(Props(classOf[CollectorActor], graph), "main-actor")
    val crawlerMaster = crawlSystem.actorOf(Props(classOf[CrawlerSystem.CrawlerWorker], collector), "crawler-master")

//    implicit val timeout = Timeout(5, TimeUnit.MINUTES)
    val future = crawlerMaster ! StartCrawling(url)
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
