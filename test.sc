val site = "golem.de"
val doc = scala.io.Source.fromURL("http://" + site).mkString
val pattern = "<a.*?</a>".r
val links = pattern.findAllIn(doc)
val linkPattern = """href=["'](.+?)["'].*?>(.*?)<""".r.unanchored
var linkMap = links.map(link => {
  link match {
    case linkPattern(url, text) => url -> text
    case _ => "wat" -> "waaaat"
  }
}).toMap

// remove anchor links
linkMap = linkMap.filterKeys(!_.startsWith("#"))
println(linkMap.size)
val internal = linkMap.filterKeys(key => key.contains(site) || key.startsWith("/"))
val external = (linkMap.toSet diff internal.toSet).toMap
val mailAdresses = linkMap.filterKeys(_.startsWith("mailto"))
external.foreach((k) => println(k._1 + "-->" + k._2))
