package crawling

import java.net.URL
import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import crawling.Messages._
import webgraph.{Weblink, Webpage}

import scala.concurrent.Await

object CrawlerSystem extends App{
  val crawlSystem = ActorSystem("crawlSystem", ConfigFactory.load.getConfig("crawlsystem"))

  class CrawlerWorker(collector : ActorRef) extends Actor{
    val selectionPattern = "<a.*?</a>".r
    val linkPattern = """href=["'](.+?)[#"'].*?>(.*?)<""".r.unanchored

    def receive = {
      case StartCrawling(url) => startCrawling(url)
      case CrawlPage(parent, url, currentDepth, visited) => crawlSubPage(parent, url, currentDepth, visited)
    }

    private def crawlSubPage(parent : Webpage, url : URL, currentDepth : Int, visited : Set[URL]) = {
//      val page2 = Webpage(url.toString, parent.url)
//      page2 match {
//        case Some(link) => link match {
//          case InternalWebpage(_) => println("internal")
//          case ExternalWebpage(_) => println("external")
//        }
//        case None => println("error")
//      }
      collector ! BeginThread
      val page = Webpage(url)
      try{
        page.content = downloadPage(url)
        page.analyze(CrawlPrefs.analyzeFunctionsPages)
        val link = Weblink(parent, page)
        collector ! CrawlResult(link)
        implicit val timeout = Timeout(20, TimeUnit.SECONDS)
        val future = collector ? Visited(visited)
        val v = Await.result(future, timeout.duration)
        if(currentDepth <= CrawlPrefs.maxDepth /*&& !v.asInstanceOf[Visited].urls.contains(url)*/){
          val map = extractInternalLinks(page.content, url)
          val newVisited = visited ++ map.map{case (link, label) => buildUrl(url, link)}.toSet
//          var futures = List[Future[Any]]()
          for((link, label) <- map) {
            val name = safeActorName(s"$currentDepth-$link")
            val worker = createWorkerActor(name)
            val absoluteUrl = buildUrl(url, link)
            worker ! CrawlPage(page, absoluteUrl, currentDepth + 1, newVisited - absoluteUrl)

            //          implicit val timeout = Timeout(60, TimeUnit.SECONDS)
            //          val future = worker ? CrawlSubPage(page, absoluteUrl, currentDepth + 1, newVisited - absoluteUrl)
            //          futures = futures :+ future
            //          sender ! Await.result(future, timeout.duration)
          }
          //        sequence(futures)
        }
      } catch{
        case e : Exception => println("a")
      } finally {
        collector ! EndThread
      }
    }

    private def crawlSubPage(url : URL, parent : Webpage) = {
      collector ! BeginThread
//      val page =
      /**
        * download page
        * send result to collector
        * send notification to sender
        */
      collector ! EndThread
    }

//
//    private def crawlSubPage2(parent : Webpage, url : String, currentDepth : Int, visited : Set[URL]) = {
//      val p = Webpage(url, parent.url)
//      if(p.isDefined){
//        p.get match {
//          case page : InternalWebpage =>{
//            val html = downloadPage2(page.url)
//            page.content = html
//            page.analyze(CrawlPrefs.analyzeFunctionsPages)
//            val link = Weblink(parent, page)
//            collector ! CrawlResult(link)
//            if(currentDepth <= CrawlPrefs.maxDepth && !visited.contains(page.url)){
//              val websites = extractAllLinks2(html, parent)
//
//            }
//          }
//        }
//      }
//
//    }

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

    private def createWorkerActor(name : String) : ActorRef = {
      context.actorOf(Props(classOf[CrawlerWorker], collector), name = name)
    }

    private def downloadPage(url : URL) : String = {
      scala.io.Source.fromURL(url).mkString
    }

//    private def downloadPage2(url : URL) : String = {
//      var html = ""
//      try{
//        html = scala.io.Source.fromURL(url).mkString
//      } catch {
//        case e: Exception => println("Error while parsing " + url.toString + " " + e.getMessage)
//      }
//      html
//    }

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

//    private def extractAllLinks2(html : String, parent : Webpage) : List[Webpage] = {
//      val links = selectionPattern.findAllIn(html)
//      var ret = List[Webpage]
//
//      val linkSet = links.map(link => {
//        link match {
//          case linkPattern(url, text) => {
//            val a = Webpage(url, parent.url)
//            if(a.isDefined) a
//          }
//          case _ => null
//        }
//      }).toList
//      linkSet
//    }
  }
}
