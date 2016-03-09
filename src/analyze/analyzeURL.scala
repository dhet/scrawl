package analyze

import java.net.URL

import crawling.CrawlPrefs

/**
  * This Object contains the distance and similarity functions to compare two URLs
  * (Currently it is used in Webgraph -> sitstructure to generate the most likely sitestructure by determining single parent webpages for every webpage)
  */
object AnalyzeURL {

  /**
    * Returns the distance between two urls based on their similarity
    * @param url1 first url
    * @param url2 second url
    * @return distance is double
    */
  def distance(url1: URL, url2: URL) : Double = {
    val sim = similarity(url1.toString, url2.toString) //due to the fact that distance may be 0 and to not calculate distance twice
    if(sim != 0)
      1 / sim
    else
      100 //just a big heuristic value
  }

  /**
    * Returns the similarity (between 0 and 1) presented by Xiaoguang Qi, Lan Nie and Brian D. Davison in http://www2007.org/workshops/paper_103.pdf
    * @param url1 first url
    * @param url2 second url
    * @return similarity of the two urls
    */
  def similarity(url1: String, url2: String) : Double = {
    val substringSize = CrawlPrefs.similarityAdjustment //could be specified with a flag later on - results in little changes of the sitestructure
    if(url1.length > substringSize || url2.length >substringSize)
      return  (2 * set(url1, substringSize).intersect(set(url2, substringSize)).size.asInstanceOf[Double]) / set(url1, substringSize).union(set(url2, substringSize)).size.asInstanceOf[Double]
    0
  }

  /**
    * prepares sets for similarity check
    * @param s the url as string
    * @param r the size of the substrins
    * @return set of substrings
    */
  private def set(s: String, r : Int) : Set[String] = {
    var substring = Set[String]()
    for(i <- 1 to s.length - r){
      substring += s.substring(i, i+r)
    }
    substring
  }
}
