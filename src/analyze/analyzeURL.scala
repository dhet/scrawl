package analyze

import java.net.URL

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
  def distance(url1: URL, url2: URL) : Double = 1 / similarity(url1, url2)

  /**
    * Returns the similarity (between 0 and 1) presented by Xiaoguang Qi, Lan Nie and Brian D. Davison in http://www2007.org/workshops/paper_103.pdf
    * @param url1 first url
    * @param url2 second url
    * @return similarity of the two urls
    */
  def similarity(url1: URL, url2: URL) : Double = {
    val substringSize = 3 //could be specified with a flag later on - results in little changes of the sitestructure
    if(url1.toString.length > substringSize || url2.toString.length >substringSize)
      return set(url1.toString, substringSize).intersect(set(url2.toString, substringSize)).size.asInstanceOf[Double] / set(url1.toString, substringSize).union(set(url2.toString, substringSize)).size.asInstanceOf[Double]
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
