package crawling

import java.net.URL
import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.dispatch.Futures
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import crawling.Messages._
import webgraph.{ExternalWebpage, InternalWebpage, Weblink, Webpage}
import akka.pattern.ask

import scala.concurrent.{ExecutionContext, Future, Await}

object CrawlerSystem extends App{
  val crawlSystem = ActorSystem("crawlSystem", ConfigFactory.load.getConfig("crawlsystem"))

  class CrawlerWorker(collector : ActorRef) extends Actor{
    val selectionPattern = "<a.*?</a>".r
    val linkPattern = """href=["'](.+?)[#"'].*?>(.*?)<""".r.unanchored

    def receive = {
      case StartCrawling(url) => startCrawling(url)
      case CrawlPage(parent, url, currentDepth, visited) => crawl(parent, url, currentDepth, visited)
      case CrawlSubPage(url, parent) => crawlSinglePage(url, parent)
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
        val v = Await.result(future, timeout.duration).asInstanceOf[Visited].urls
        if(currentDepth <= CrawlPrefs.maxDepth && !visited.contains(url)/* && !v.contains(url)*/){
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

    private def crawl(parent : Webpage, url : URL, currentDepth : Int, visited : Set[URL]) : Unit = {
      if(currentDepth <= CrawlPrefs.maxDepth && !visited.contains(url)){
        val links = extractInternalLinks(parent.content, url).map{case (link, label) => buildUrl(url, link)}.toSet
        var futures = Set[Future[SendCrawlResult]]()
        implicit val timeout = Timeout(300, TimeUnit.SECONDS)
        implicit val ec : ExecutionContext = ExecutionContext.fromExecutor(context.dispatcher)
        val combined = Future.traverse(links -- visited)(link =>{
          val name = safeActorName(s"$currentDepth-${link.toString}")
          val worker = createWorkerActor(name)
          val absoluteUrl = buildUrl(url, link.toString)
          var future = Future(SendCrawlResult(None))
          if(!visited.contains(link)){
            future = ask(worker, CrawlSubPage(absoluteUrl, parent)).mapTo[SendCrawlResult]
            futures = futures + future
          } else{
            println("skipping link " + link.toString)
          }
          future
        })

        var visited2 = visited
        // block
        Await result (combined, timeout.duration)
        println(s"Done crawling level $currentDepth")
        println(futures.size)
        if(currentDepth == CrawlPrefs.maxDepth){
//          println("done")
//          collector ! DoneCrawling
        } else{
          for(future <- futures){
            future.value match{
              case Some(res) => {
                try{
                  val link = res.get.link
                  link match {
                    case Some(l) => {
                      crawl(l.startNode, l.endNode.url, currentDepth + 1, visited ++ links - l.endNode.url)
                      visited2 = visited2 + l.endNode.url
                    }
                  }
                } catch{
                  case e : Exception => println(e.getMessage)
                }
              }
              case None => println("asdf")
            }
          }
        }
      }
    }

    private def crawlSinglePage(url : URL, parent : Webpage) = {
      collector ! BeginThread
      val page = Webpage(url)
      try{
        page.content = downloadPage(url)
        page.analyze(CrawlPrefs.analyzeFunctionsPages)
        val link = Weblink(parent, page)
        collector ! CrawlResult(link)
        sender ! SendCrawlResult(Some(link))
      } catch{
        case e : Exception => sender ! SendCrawlResult(None)
      } finally{
        collector ! EndThread
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
      crawl(root, url, 1, Set[URL]())
    }

    private def buildUrl(base : URL, path : String) = {
      new URL(base, path)
    }

    private def safeActorName(name : String) : String = {
      name.replaceAll("""[^\w]""", "~")
    }

    private def createWorkerActor(name : String) : ActorRef = {
      println(name)
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
