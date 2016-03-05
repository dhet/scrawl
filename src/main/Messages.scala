package main

import java.net.URL

import Webgraph.Webgraph

object Messages {
  case class CrawlPage(url : URL, prefs : CrawlPrefs)
  case class CrawlSubPage(url : URL, prefs : CrawlPrefs, currentDepth : Int, visited : Set[URL])
  case class CrawlResult(graph : Webgraph)
}
