package crawling

import java.net.URL
import java.nio.file.{Files, Paths}
import java.util.concurrent.TimeUnit

import akka.actor.{Props, Actor, ActorRef, ActorSystem}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import crawling.CrawlerSystem.CrawlerWorker
import crawling.Messages.{DoneCrawling, CrawlResult, StartCrawling}
import webgraph.{Webpage, Webgraph, Weblink}
import akka.pattern.ask

import scala.concurrent.Await
import scala.reflect.io.File
import scala.xml.PrettyPrinter

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
    var counter = 0
    def receive = {
      case CrawlResult(link) => {
        println(s"added link ${link.startNode.url} -> ${link.endNode.url}")
        graph.addWeblink(link)
        counter += 1
        if(counter == 10){
          context.self ! DoneCrawling
        }
      }
      case DoneCrawling => saveGraphToDisk()
    }

    def saveGraphToDisk() : Unit ={
      val printer = new PrettyPrinter(300, 2)
      val content = printer.format(graph.xml).getBytes()
      val filename = graph.root.url.getHost.replace("[.\\]", "_") + ".xml"
      val file = CrawlPrefs.outDir.toFile
      file.mkdirs()
      val resultingPath = new java.io.File(file, filename)
      Files.write(resultingPath.toPath, content)
      println(s"File saved to ${resultingPath.getAbsolutePath}.")
    }
  }
}
