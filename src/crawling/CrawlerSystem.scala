package crawling

import java.net.URL
import java.util.concurrent.TimeUnit
import akka.pattern.ask
import akka.actor.Status.{Failure, Success}
import akka.actor.{ActorRef, Props, Actor, ActorSystem}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import crawling.CollectorSystem.CollectorActor
import crawling.Messages.{CrawlResult, CrawlSubPage, StartCrawling}
import webgraph.{Webpage, Weblink}

import scala.collection.immutable.HashSet
import scala.concurrent.Await

object CrawlerSystem extends App{
  val crawlSystem = ActorSystem("crawlSystem", ConfigFactory.load.getConfig("crawlsystem"))

  class CrawlerWorker(collector : ActorRef) extends Actor{
    val selectionPattern = "<a.*?</a>".r
    val linkPattern = """href=["'](.+?)[#"'].*?>(.*?)<""".r.unanchored

    def receive = {
      case StartCrawling(url) => startCrawling(url)
      case CrawlSubPage(parent, url, currentDepth, visited) => crawlSubPage(parent, url, currentDepth, visited)
    }

    private def crawlSubPage(parent : Webpage, url : URL, currentDepth : Int, visited : Set[URL]) = {
//      println("crawling " + parent.url)
      val page = downloadPage(url)
      if(currentDepth <= CrawlPrefs.maxDepth && !visited.contains(url)){
        page.analyze(CrawlPrefs.analyzeFunctions)
        val link = Weblink(parent, page)
        collector ! CrawlResult(link)
        val map = extractInternalLinks(page.content, url)
//        println(s"$currentDepth crawled " + url.toString)
        val newVisited = visited ++ map.map{case (link, label) => buildUrl(url, link)}.toSet
        for((link, label) <- map) {
          val name = safeActorName(s"$currentDepth-$link")
          val worker = createWorker(name)
          val absoluteUrl = buildUrl(url, link)
          worker ! CrawlSubPage(page, absoluteUrl, currentDepth + 1, newVisited.diff(Set(absoluteUrl)))
//          implicit val timeout = Timeout(60, TimeUnit.SECONDS)
//          val future = worker ? CrawlSubPage(page, absoluteUrl, currentDepth + 1)
//          sender ! Await.result(future, timeout.duration)
        }
      }
    }

    private def startCrawling(url : URL) = {
      val root = new Webpage(url)
      crawlSubPage(root, url, 1, Set[URL]())
    }

    private def buildUrl(base : URL, path : String) = {
      new URL(base, path)
    }

    private def safeActorName(name : String) : String = {
      name.replaceAll("""[^\w]""", "~")
    }

    private def createWorker(name : String) : ActorRef = {
      context.actorOf(Props(classOf[CrawlerWorker], collector), name = name)
    }

    private def downloadPage(url : URL) : Webpage = {
      var html = ""
      try{
        html = scala.io.Source.fromURL(url).mkString
      } catch {
        case e: Exception => println("Error while parsing " + url.toString + " " + e.getMessage)
      }
      new Webpage(url, html)
    }

    private def extractInternalLinks(html : String, baseSite : URL) = {
      extractAllLinks(html).filterKeys(key => key.contains(baseSite.getHost) || key.startsWith("/"))
    }

    private def extractAllLinks(html : String) : Map[String, String] = {
      val links = selectionPattern.findAllIn(html)
      val linkMap = links.map(link => {
        link match {
          case linkPattern(url, text) => url -> text
          case _ => "wat" -> "waaaat"
        }
      }).toMap

      // remove anchor links and mail links
      linkMap.filterKeys(link => !link.startsWith("#") && !link.startsWith("mailto"))
    }
  }
}
