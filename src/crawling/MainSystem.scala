package crawling

import java.net.URL

import akka.actor.{Props, Actor, ActorRef, ActorSystem}
import com.typesafe.config.ConfigFactory
import crawling.CrawlerSystem.CrawlerMaster
import crawling.Messages.{CrawlResult, StartCrawling}

object MainSystem extends App{
  def crawlPage(url : URL) = {
    val mainSystem = ActorSystem("crawling", ConfigFactory.load.getConfig("mainsystem"))
    val crawlerMaster = mainSystem.actorOf(Props[CrawlerMaster])
    val mainActor = mainSystem.actorOf(Props(classOf[MainActor], crawlerMaster))
    mainActor ! StartCrawling(url,  CrawlPrefs)
  }

  class MainActor(crawlerMaster: ActorRef) extends Actor{
    def receive = {
      case StartCrawling(url, prefs) => crawlerMaster ! StartCrawling(url, prefs)
      case CrawlResult(result) => println(result.toXML())
    }
  }
}
