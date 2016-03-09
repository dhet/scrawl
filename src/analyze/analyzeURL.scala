package analyze

import java.net.URL

/**
  * Created by nicohein on 09/03/16.
  */
object AnalyzeURL {

  /**
    * Returns the distance between two urls based on their similarity
 *
    * @param url1 first url
    * @param url2 second url
    * @return distance is double
    */
  def distance(url1: URL, url2: URL) : Double = 1 / similarity(url1, url2)

  /**
    * Returns the similarity (between 0 and 1) presented by Xiaoguang Qi, Lan Nie and Brian D. Davison in http://www2007.org/workshops/paper_103.pdf
 *
    * @param url1 first url
    * @param url2 second url
    * @return similarity of the two urls
    */
  def similarity(url1: URL, url2: URL) : Double = {
    if(url1.toString.length >3 || url2.toString.length >3)
      return set(url1.toString, 3).intersect(set(url2.toString, 3)).size.asInstanceOf[Double] / set(url1.toString, 3).union(set(url2.toString, 3)).size.asInstanceOf[Double]
    return 0
  }

  /**
    * prepares sets for similarity check
 *
    * @param s the url as string
    * @param r the size of the substrins
    * @return set of substrings
    */
  private def set(s: String, r : Int) : Set[String] = {
    var substring = Set[String]()
    for(i <- 1 to s.length-r){
      substring += s.substring(i, i+r)
    }
    substring
  }
}
