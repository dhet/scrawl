package crawling

import java.net.URL
import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import crawling.Messages._
import webgraph.{Weblink, Webpage}

import scala.concurrent.{ExecutionContext, Future, Await}

object CrawlerSystem extends App{
  val crawlSystem = ActorSystem("crawlSystem", ConfigFactory.load.getConfig("crawlsystem"))

  class CrawlerMaster(collector : ActorRef) extends Actor{
    val selectionPattern = "<a.*?</a>".r
    val linkPattern = """href=["'](.+?)[#"'].*?>(.*?)<""".r.unanchored

    def receive = {
      case StartCrawling(url) => startCrawling(url)
      case CrawlPage(parent, currentDepth, visited) => crawl(parent, currentDepth, visited)
      case CrawlSubPage(url, parent) => crawlSinglePage(url, parent)
    }

    private def crawl(parent : Webpage, currentDepth : Int, visited : Set[URL]) : Set[URL] = {
      if(currentDepth <= CrawlPrefs.maxDepth){
        val links = extractInternalLinks(parent.content, parent.url).map{case (link, label) => buildUrl(parent.url, link)}.toSet
        var futures = Set[Future[SendCrawlResult]]()
        implicit val timeout = Timeout(300, TimeUnit.SECONDS)
        implicit val ec : ExecutionContext = ExecutionContext.fromExecutor(context.dispatcher)
        val combined = Future.traverse(links.drop(links.size - CrawlPrefs.limit))(link =>{
          val absoluteUrl = buildUrl(parent.url, link.toString)
          var future = Future(SendCrawlResult(None))
          if(!visited.exists(url => url.equals(link))){
            val name = safeActorName(s"$currentDepth-${link.toString}")
            val worker = createWorkerActor(name)
            future = ask(worker, CrawlSubPage(absoluteUrl, parent)).mapTo[SendCrawlResult]
            futures = futures + future
          } else{
            val endNode = Webpage(link)
            if(!endNode.url.equals(parent.url)) collector ! CrawlResult(Weblink(parent, endNode))
            println("skipping link " + link.toString)
          }
          future
        })

        var visited2 = visited ++ links
        // block until all pages are downloaded and analyzed
        Await result (combined, timeout.duration)
//        println(s"Done crawling level $currentDepth")
//        println(s"${futures.size} jobs")

        for(future <- futures.filter(f => f.value.isDefined); link <- future.value.get.get.link){
          visited2 = visited2 ++ crawl(link.endNode, currentDepth + 1, visited2 - link.startNode.url)
          visited2 = visited2 + link.endNode.url
        }
        links
      } else{
        Set[URL]()
      }
    }

    private def crawlSinglePage(url : URL, parent : Webpage) : Option[Weblink] = {
      val page = Webpage(url)
      try{
        page.content = downloadPage(url)
        page.analyze(CrawlPrefs.analyzeFunctionsPages)
        val link = Weblink(parent, page)
        if(!page.url.equals(parent.url)) collector ! CrawlResult(link)
        sender ! SendCrawlResult(Some(link))
        Some(link)
      } catch{
        case e : Exception => {
          sender ! SendCrawlResult(None)
          None
        }
      } finally{
//        sender ! SendCrawlResult(None)
      }
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
      root.content = downloadPage(url)
      root.analyze(CrawlPrefs.analyzeFunctionsPages)
      val visited = crawl(root, 1, Set[URL]())
      println("crawled " + visited.size + " Pages")
      collector ! DoneCrawling
    }

    private def buildUrl(base : URL, path : String) = {
      new URL(base, path)
    }

    private def safeActorName(name : String) : String = {
      name.replaceAll("""[^\w]""", "~")
    }

    private def createWorkerActor(name : String) : ActorRef = {
//      println(name)
      context.actorOf(Props(classOf[CrawlerMaster], collector), name = name + UUID.randomUUID().toString)
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
          case linkPattern(url, text) => url.stripSuffix("/") -> text
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

    class CrawlerWorker(collector : ActorRef) extends Actor{
      override def receive = {
        case CrawlSubPage(url, parent) => crawlPage(url, parent)
      }

      private def crawlPage(url : URL, parent : Webpage) = {
        val page = Webpage(url)
        try{
          page.content = downloadPage(url)
          page.analyze(CrawlPrefs.analyzeFunctionsPages)
          val link = Weblink(parent, page)
          if(!page.url.equals(parent.url)) collector ! CrawlResult(link)
          sender ! SendCrawlResult(Some(link))
        } catch{
          case e : Exception => {
            sender ! SendCrawlResult(None)
          }
        } finally{
          sender ! SendCrawlResult(None)
        }
      }

      private def downloadPage(url : URL) : String = {
        scala.io.Source.fromURL(url).mkString
      }

    }
  }
}
