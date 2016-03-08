package crawling

import java.nio.file.Paths

import com.sun.deploy.util.StringUtils
import graph.LabelEntry
import webgraph.{Weblink, Webpage}

object CrawlPrefs {
  var maxDepth = 2
  var threads = 3
  var analyzeFunctionsPages = Seq[(Webpage) => Option[LabelEntry]]()
  var analyzeFunctionsLinks = Seq[(Weblink) => Option[LabelEntry]]()
  var outDir = Paths.get("./generated")

  addPageAnalyzeFunction(extractPageTitle)

  def addPageAnalyzeFunction(alg : (Webpage) => Option[LabelEntry]) = {
    analyzeFunctionsPages = analyzeFunctionsPages :+ alg
  }

  private def extractPageTitle(page : Webpage) : Option[LabelEntry] = {
    val selectionPattern = """<title>(.*?)</title>""".r.unanchored
    selectionPattern.findFirstMatchIn(page.content) match{
      case Some(title) => Some(LabelEntry("page-title", title.group(1)))
      case None => None
    }
  }
}




