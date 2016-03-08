package cli

import java.nio.file.Paths

import crawling.CrawlPrefs
import graph.LabelEntry
import webgraph.Webpage

object Argument {
  var supportedArgs = Set[Argument]()

  supportedArgs += new ParamArgument("level"){
      synonyms = List[String] ("l", "d", "depth")
      helpText = "Determines how many levels to crawl. Expects one argument of type Integer."
      override def action = {
        CrawlPrefs.maxDepth = parameter.toString.toInt
      }
    }

  supportedArgs += new Flag("help") {
    synonyms = List[String]("h", "?", "wat")
    helpText = "Print this help."
    override def action = {
      println("Usage: scrawl url [url, ...][option, ...]")
      for (arg <- supportedArgs) {
        println(s"-${arg.name} (${arg.synonyms.map(a => s"-$a").mkString(", ")})")
        println(s"\t${arg.helpText}")
      }
    }
  }

  supportedArgs += new ParamArgument("out"){
    synonyms = List[String]("o, path, dir")
    helpText = "Specifies the directory where to save the sitemap(s). The files are saved in the ./sitemaps directory" +
      "by default. The name of the xml files correspond to the crawled sites."
    override def action = {
      CrawlPrefs.outDir = Paths.get(parameter.toString)
    }
  }

  supportedArgs += new ParamArgument("threads") {
    synonyms = List[String]("t")
    helpText = "Specifies ow many threads to use for crawling. Expects one argument of type Integer."
    override def action = {
      CrawlPrefs.threads = parameter.toString.toInt
    }
  }

  supportedArgs += new Flag("words"){
    synonyms = List[String]("wc")
    helpText = "Count the number of words in every website."
    override def action = {
      CrawlPrefs.addPageAnalyzeFunction((webpage) => {
        Some(LabelEntry("words", webpage.content.split(" ").size.toString))
      })
    }
  }

  def printHelp = {
    supportedArgs.find(_.name equals "help") match{
      case Some(arg) => arg.action
      case None => println("No help defined.")
    }
  }

  /**
    * Argument factory. Determine the type of an argument and return an appropriate object. If the type can't be
    * determined return an object of type {@link InvalidArgument}
    *
    * @param arg  The argument you're looking for
    * @return     An object of a subclass of {@link Argument}
    */
  def apply(arg : String) : Argument = {
    if(arg.startsWith("-")) {
      supportedArgs.find(a => a.name == arg.substring(1) || a.synonyms.contains(arg.substring(1))) match {
        case None => new InvalidArgument(arg)
        case Some(argument) => argument
      }
    }
    else {
      new Value(arg)
    }
  }
}


abstract class Argument(val name : String){
  if(!isValid) throw new IllegalArgumentException

  var synonyms : List[String] = List[String]()
  var helpText : String = _
  def isValid = true
  def action = println(s"No action for argument '$name' defined.")
  override def equals(obj : Any) = obj match {
    case Argument => obj.asInstanceOf[Argument].name == name ||
      synonyms.contains(obj.asInstanceOf[Argument].name)
    case _ => false
  }
}

case class Flag(override val name : String) extends Argument(name){
  override def toString = s"-$name"
}

case class Value(override val name : String) extends Argument(name){
  override val toString = name
}

case class ParamArgument(override val name : String) extends Argument(name){
  var parameter : Argument = InvalidArgument("MISSING_ARGUMENT")
  override def toString = s"-$name $parameter"
}

case class InvalidArgument(override val name : String) extends Argument(name){
  override def action = throw new UnsupportedOperationException(s"The Argument $name is not supported.")
  override def toString = name
}
