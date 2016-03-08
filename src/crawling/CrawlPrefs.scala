package crawling

import com.sun.deploy.util.StringUtils
import graph.LabelEntry
import webgraph.{Weblink, Webpage}

object CrawlPrefs {
  var maxDepth = 2
  var threads = 3
  var analyzeFunctionsPages = Seq[(Webpage) => Option[LabelEntry]]()
  var analyzeFunctionsLinks = Seq[(Weblink) => Option[LabelEntry]]()

  addPageAnalyzeFunction(extractPageTitle)

  def addPageAnalyzeFunction(alg : (Webpage) => Option[LabelEntry]) = {
    analyzeFunctionsPages = analyzeFunctionsPages :+ alg
  }

  private def extractPageTitle(page : Webpage) : Option[LabelEntry] = {
    val selectionPattern = """<title>(.*?)</title>""".r.unanchored
    selectionPattern.findFirstMatchIn(page.content) match{
      case Some(title) => Some(LabelEntry("PageTitle", title.group(1)))
      case None => None
    }
  }
}




