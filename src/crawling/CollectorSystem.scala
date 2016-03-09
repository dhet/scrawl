package crawling

import java.net.URL
import java.nio.file.{Files, Paths}
import java.util.concurrent.TimeUnit

import akka.actor.{Props, Actor, ActorRef, ActorSystem}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import crawling.CrawlerSystem.CrawlerMaster
import crawling.Messages._
import webgraph.{Webpage, Webgraph, Weblink}
import akka.pattern.ask

import scala.concurrent.Await
import scala.reflect.io.File
import scala.xml.PrettyPrinter

object CollectorSystem{
  val collectorSystem = ActorSystem("collectorsystem", ConfigFactory.load.getConfig("collectorsystem"))
  val crawlSystem = ActorSystem("crawlsystem", ConfigFactory.load.getConfig("crawlsystem"))

  def crawlPage(url : URL) = {
    val graph = Webgraph(Webpage(url))
    val collector = collectorSystem.actorOf(Props(classOf[CollectorActor], graph), "collector")
    val crawlerMaster = crawlSystem.actorOf(Props(classOf[CrawlerSystem.CrawlerMaster], collector), "crawler")

    crawlerMaster ! StartCrawling(url)

//    implicit val timeout = Timeout(5, TimeUnit.MINUTES)
//    val future = collector ? StartCrawling(url)
//    val result = Await.result(future, timeout.duration)
//    graph
  }

  class CollectorActor(graph : Webgraph) extends Actor{

    def receive = {
      case CrawlResult(link) => {
        println(link)
        graph.addWeblink(link)
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
