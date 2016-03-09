package crawling

import java.nio.file.Paths

import graph.LabelEntry
import webgraph.{Weblink, Webpage}

/**
  * Object that contains all crawler properties. The properties should only be defined before the crawling process has
  * started. The crawler gets the preferences during the process as needed.
  * <br/><br/>
  * This object contains a list of analyze functions. Each of the functions is called by the crawler once a site has
  * been crawled. Analyze functions have the signature
  * `([[webgraph.Webpage]]) => [[scala.Option]] [ [[graph.LabelEntry]] ]`.
  */
object CrawlPrefs {
  var maxDepth = 2
  var analyzeFunctionsPages = Seq[(Webpage) => Option[LabelEntry]]()
  var analyzeFunctionsLinks = Seq[(Weblink) => Option[LabelEntry]]()
  var outDir = Paths.get("./sitemaps")

  addPageAnalyzeFunction(extractPageTitle)

  /**
    * Add an analyze function to the list of analyze functions
    * @param alg  The function to add
    */
  def addPageAnalyzeFunction(alg : (Webpage) => Option[LabelEntry]) = {
    analyzeFunctionsPages = analyzeFunctionsPages :+ alg
  }



  /**
    *
    * Default analyze functions
    *
    */

  /**
    * Extract the page title from a given website's HTML via regular expressions. The page title can be found between
    * the {{{<title>}}} tags.
    * @param page The page to analyze
    * @return     An optional link label containing ''page-title'' as key and the extracted page title as value
    */
  private def extractPageTitle(page : Webpage) : Option[LabelEntry] = {
    val selectionPattern = """<title>(.*?)</title>""".r.unanchored
    selectionPattern.findFirstMatchIn(page.content) match{
      case Some(title) => Some(LabelEntry("page-title", title.group(1)))
      case None => None
    }
  }
}

