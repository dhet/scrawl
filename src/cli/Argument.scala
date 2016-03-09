package cli

import java.nio.file.Paths

import crawling.CrawlPrefs
import graph.LabelEntry

/**
  * Companion object of [[cli.Argument]]. The list of supported command line arguments is defined here (including their
  * behavior upon interpretation) as well as a factory method that decides of which type an argument is based on an
  * input string and returns a corresponding object.
  */
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

  /**
    * Print the help text. finds the `help` argument in the list of supported arguments executes it.
    */
  def printHelp = {
    supportedArgs.find(_.name equals "help") match{
      case Some(arg) => arg.action
      case None => println("No help defined.")
    }
  }

  /**
    * Argument factory. Determines the type of an argument based on an input string and returns an appropriate object.
    * If the type can't be determined return an object of type [[cli.InvalidArgument]]
    *
    * @param arg  The input argument
    * @return     An object of a subclass of [[cli.Argument]]
    */
  def apply(arg : String) : Argument = {
    if(arg.startsWith("-")) {
      isCommandSupported(arg) match {
        case Some(argument) => argument
        case None => new InvalidArgument(arg)
      }
    }
    else {
      new Value(arg)
    }
  }

  /**
    * Helper function to determine whether a given argument string is supported.
    * @param cmd  The command string
    * @return     The argument if it is supported or [[scala.None]]
    */
  private def isCommandSupported(cmd : String) : Option[Argument] = {
    supportedArgs.find(argument => {
      argument.name == cmd.substring(1) || argument.synonyms.contains(cmd.substring(1))
    })
  }
}


/**
  * An Argument represents a token in a command line input string. E.g. the command
  * {{{crawl www.example.com -wc -d 4 -undefined}}} contains 5 Arguments: `www.example.com` of type [[cli.Value]];
  * `-wc` of type [[cli.Flag]]; `-d` of type [[cli.ParamArgument]]; `4` of type [[cli.Value]] where `4` is part of the
  * preceding `-d` as it is its parameter; and `-undefined` of type [[cli.InvalidArgument]] as it isn't defined.
  * @param name
  */
abstract class Argument(val name : String){
  var synonyms : List[String] = List[String]()
  var helpText : String = _
  def isValid = true

  /**
    * The action to be performed once the whole command has been parsed and is being interpreted.
    */
  def action = println(s"No action for argument '$name' defined.")

  /**
    * Object equality is given if the other object is of type [[cli.Argument]] and if its name either matches this
    * object's name or any of the synonyms.
    * @param obj  The other object to check
    * @return     `true` if the other argument's name equals this argument's name or any of its synonyms
    */
  override def equals(obj : Any) = obj match {
    case Argument => obj.asInstanceOf[Argument].name == name ||
      synonyms.contains(obj.asInstanceOf[Argument].name)
    case _ => false
  }
}

/**
  * Represents tokens that are interpreted as flags, i.e. not followed by a parameter. E.g. `-help -words` are two flags.
  * @param name The primary command name
  */
case class Flag(override val name : String) extends Argument(name){
  override def toString = s"-$name"
}

/**
  * Represents any token that is not preceded by a dash character. May represent actual input arguments or parameters
  * for [[cli.ParamArgument]]s.
  * @param name The token itself
  */
case class Value(override val name : String) extends Argument(name){
  override val toString = name
}

/**
  * Represents an command line argument that is followed by a single parameter, e.g. {{{-depth 3}}} where `-depth` is the
  * argument itself and `3` its parameter.
  * @param name The primary command name
  */
case class ParamArgument(override val name : String) extends Argument(name){
  var parameter : Argument = InvalidArgument("MISSING_ARGUMENT")
  override def toString = s"-$name $parameter"
}

/**
  * Represents any invalid argument. Invalid arguments are undefined in the program.
  * @param name The name of the argument
  */
case class InvalidArgument(override val name : String) extends Argument(name){
  override def action = throw new UnsupportedOperationException(s"The Argument $name is not supported.")
  override def toString = name
}
