package crawling

import java.net.URL

import akka.actor.{Props, Actor, ActorSystem}
import com.typesafe.config.ConfigFactory
import crawling.Messages.{CrawlSubPage, CrawlPage}

import scala.collection.immutable.HashSet

object CrawlerSystem extends App{
  val crawlSystem = ActorSystem("crawlSystem", ConfigFactory.load.getConfig("crawlsystem"))

  class CrawlerMaster extends Actor{
    val selectionPattern = "<a.*?</a>".r
    val linkPattern = """href=["'](.+?)["'].*?>(.*?)<""".r.unanchored

    def receive = {
      case CrawlPage(url, crawlPrefs) => crawlPage(url, crawlPrefs)
      case CrawlSubPage(url, crawlPrefs, currentDepth, visited) => crawlSubPage(url, crawlPrefs, currentDepth, visited)
      case "hi" => println("sup?")
    }

    private def crawlSubPage(url : URL, crawlPrefs : CrawlPrefs.type , currentDepth : Int, visited : Set[URL]) = {
      if(currentDepth <= crawlPrefs.maxDepth && !visited.contains(url)){
        val html = downloadPage(url)
        val map = extractInternalLinks(html, url)
        sender() ! "hi"
        println(s"$currentDepth crawled " + url.toString)
        for((link, label) <- map) {
          val name = safeActorName(s"$currentDepth-$link")
          val worker = createWorker(name)
          val absoluteUrl = buildUrl(url, link)
          worker ! CrawlSubPage(absoluteUrl, crawlPrefs, currentDepth + 1, visited)
        }
      }
    }

    private def crawlPage(url : URL, crawlPrefs : CrawlPrefs.type ) = {
      crawlSubPage(url, crawlPrefs, 1, new HashSet[URL]())
    }

    private def buildUrl(base : URL, path : String) = {
      new URL(base, path)
    }

    private def safeActorName(name : String) = {
      name.replaceAll("""[^\w]""", "~");
    }

    private def createWorker(name : String) = {
      context.actorOf(Props[CrawlerMaster], name = name)
    }

    private def downloadPage(url : URL) = {
      var html = ""
      try{
        html = scala.io.Source.fromURL(url).mkString
      } catch {
        case e: Exception => println("Error while parsing " + url.toString)
      }
      html
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

  class CrawlerWorker extends Actor {
    def receive = {
      case CrawlSubPage(url, crawlPrefs, depth, visited) => println(depth)
    }

  }
}
