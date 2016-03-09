package crawling

import java.net.URL
import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import crawling.Messages._
import webgraph.{Weblink, Webpage}

import scala.concurrent.{ExecutionContext, Future, Await}

/**
  * An akka system housing actors to crawl pages.
  */
object CrawlerSystem extends App{

  class CrawlerMaster(collector : ActorRef) extends Actor{
    val selectionPattern = "<a.*?</a>".r
    val linkPattern = """href=["'](.+?)[#"'].*?>(.*?)<""".r.unanchored

    def receive = {
      case StartCrawling(url) => startCrawling(url)
      case CrawlWebsite(parent, currentDepth, visited) => crawlWebsite(parent, currentDepth, visited)
      case CrawlSinglePage(url, parent) => crawlSinglePageAsync(url, parent)
    }

    /**
      * Entry point to start crawling a website. This is a blocking function and returns once the crawling process
      * has completed. In this case it sends a [[crawling.Messages.DoneCrawling]] message back to the sender.
      * @param rootUrl  Root URL of the website to crawl
      */
    private def startCrawling(rootUrl : URL) = {
      val root = new Webpage(rootUrl)
      root.content = downloadPage(rootUrl)
      root.analyze(CrawlPrefs.analyzeFunctionsPages)
      val visited = crawlWebsite(root, 1, Set[URL]())
      println("crawled " + visited.size + " Pages")
      sender ! DoneCrawling(visited.size)
    }

    /**
      * Recursively crawl a website. Each recursion step corresponds to one step in the depth of the site structure.
      * Dispatches a new worker actor for each link of a given (sub)page and waits for all actors to finish crawling
      * their respective page. Once all links were crawled the function advances to the next depth step and repeats
      * the process for each subpage. The function keeps track of the pages it has visited. If it encounters a visited
      * website it sends a [[crawling.Messages.LinkResult]] to the collector but doesn't crawl the page as it has
      * already been crawled.
      * @param page         The page to crawl
      * @param currentDepth The current recursion step (how far the function is in the site structure)
      * @param visited      The set of all crawled pages
      * @return             The updated set of crawled pages
      */
    private def crawlWebsite(page : Webpage, currentDepth : Int, visited : Set[URL]) : Set[URL] = {
      if(currentDepth <= CrawlPrefs.maxDepth){
        val links = extractInternal(page.content, page.url).map(buildUrl(page.url, _))
        var futures = Set[Future[PageCrawlResult]]()
        implicit val timeout = Timeout(300, TimeUnit.SECONDS)
        implicit val ec : ExecutionContext = ExecutionContext.fromExecutor(context.dispatcher)
        var linksToCrawl = links
        if(CrawlPrefs.limit != 0){
          linksToCrawl = linksToCrawl.drop(linksToCrawl.size - CrawlPrefs.limit)
        }
        val compositeFuture = Future.traverse(linksToCrawl)(link =>{
          val absoluteUrl = buildUrl(page.url, link.toString)
          var future = Future(PageCrawlResult(None))
          if(!visited.exists(url => url.equals(link))){
            val name = safeActorName(s"$currentDepth-${link.toString}")
            val worker = createWorkerActor(name)
            future = ask(worker, CrawlSinglePage(absoluteUrl, page)).mapTo[PageCrawlResult]
            futures = futures + future
          } else{ // skip link as it was already crawled
            val endNode = Webpage(link)
            if(!endNode.url.equals(page.url)) collector ! LinkResult(Weblink(page, endNode))
          }
          future
        })
        var newVisited = visited ++ links
        // block until all pages are downloaded and analyzed
        Await result (compositeFuture, timeout.duration)
        for(future <- futures.filter(f => f.value.isDefined); link <- future.value.get.get.link){
          val updatedVisited = crawlWebsite(link.endNode, currentDepth + 1, newVisited - link.startNode.url)
          newVisited = newVisited ++ updatedVisited + link.endNode.url
        }
        links
      } else{
        Set[URL]()
      }
    }

    /**
      * Crawl a single page (download and analyze) and construct a [[webgraph.Weblink]] from the result. Send the
      * resulting link to the collector if start and end node are not equal. Errors during the download will be ignored.
      * In any case a [[crawling.Messages.PageCrawlResult]] is sent back to the sender to notify them that the action
      * is completed. This is an autonomous, non-blocking function that can run in parallel.
 *
      * @param url    The URL of the target page
      * @param parent The page that links to the target rootUrl
      */
    private def crawlSinglePageAsync(url : URL, parent : Webpage) = {
      try{
        val page = Webpage(url)
        page.content = downloadPage(url)
        page.analyze(CrawlPrefs.analyzeFunctionsPages)
        val link = Weblink(parent, page)
        if(!page.url.equals(parent.url)) {
          collector ! LinkResult(link)
        }
        sender ! PageCrawlResult(Some(link))
      } catch{
        case e : Exception => sender ! PageCrawlResult(None)
      }
    }

    /**
      * Helper function to build a URL based on a base site
      * @param base The base site
      * @param path The URL path string
      * @return The URL
      */
    private def buildUrl(base : URL, path : String) = {
      new URL(base, path)
    }

    /**
      * Returns a unique name that can be used for actors.
      * @param name The base name
      * @return The unique name
      */
    private def safeActorName(name : String) : String = {
      name.replaceAll("[^\\w]", "~") + UUID.randomUUID().toString
    }

    /**
      * Creates a worker actor of the same type
      * @param name The name of the new actor
      * @return A reference to the new actor
      */
    private def createWorkerActor(name : String) : ActorRef = {
      context.actorOf(Props(classOf[CrawlerMaster], collector), name = name)
    }

    /**
      * Downloads a website and returns its content as string
      * @param url  The URL of the website
      * @return The page content
      */
    private def downloadPage(url : URL) : String = {
      scala.io.Source.fromURL(url).mkString
    }

    /**
      * Extract all links from a website
      * @param html The content of the website
      * @return A set of links (relative and absolute) as strings
      */
    private def extractAll(html : String) : Set[String] = {
      val links = selectionPattern.findAllIn(html)
      val linkSet = links.map(link => {
        link match{
          case linkPattern(url, text) => url.stripSuffix("/")
          case _ => "?"
        }
      }).toSet
      linkSet.filter(link => !link.startsWith("#") && !link.startsWith("mailto"))
      linkSet
    }

    /**
      * Extract all internal links from a website
      * @param html     The content of the website
      * @param baseSite The root URL of the website
      * @return A set of internal links (relative and absolute) as strings
      */
    private def extractInternal(html : String, baseSite : URL) : Set[String] = {
      extractAll(html).filter(link => link.contains(baseSite.getHost) || link.startsWith("/"))
    }
  }
}
