package crawling

import java.net.URL

import akka.actor.{Props, Actor, ActorRef, ActorSystem}
import com.typesafe.config.ConfigFactory
import crawling.CrawlerSystem.CrawlerMaster
import crawling.Messages.{CrawlResult, CrawlPage}

object MainSystem extends App{
  val mainSystem = ActorSystem("crawling", ConfigFactory.load.getConfig("mainsystem"))
  val crawlerMaster = mainSystem.actorOf(Props[CrawlerMaster])
  val mainActor = mainSystem.actorOf(Props(classOf[MainActor], crawlerMaster))

  mainActor ! CrawlPage(new URL("http://golem.de"), new CrawlPrefs(2))

  class MainActor(crawlerMaster: ActorRef) extends Actor{
    def receive = {
      case CrawlPage(url, prefs) => crawlerMaster ! CrawlPage(url, prefs)
      case CrawlResult(result) => println(result.toXML())
    }
  }
}
