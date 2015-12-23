object Argument {
  val supportedArgs : List[Argument] = List[Argument](
    new ParamArgument("analyze"){
      synonyms = List[String] ("a")
      helpText = "Analyze the crawled sites."
    }, //-a does expect a list of random variables
    new ParamArgument("level"){
      synonyms = List[String] ("l", "d", "depth")
      helpText = "Determines how many levels to crawl. Expects one argument of type Integer."
//      override def isValid = {
//        if(parameters.length == 1){
//          try{
//            Integer.parseInt(parameters.head.name)
//            true
//          } catch {
//            case ex : NumberFormatException => false
//          }
//        } else false
//      }
    }, //-L does expect a level
    new Flag("help"){
      synonyms = List[String] ("h", "?", "wat")
      helpText = "Print this help."
      override def action = {
        println("Usage: scrawl url [url, ...][option, ...]")
        for(arg <- supportedArgs){
          println(s"-${arg.name} (${arg.synonyms.map(a => s"-$a").mkString(", ")})")
          println(s"\t${arg.helpText}")
        }
      }
    }
  )

  def printHelp = {
    supportedArgs.find(arg => arg.name == "help") match{
      case Some(arg) => arg.action
      case None => println("No help defined.")
    }
  }

  /**
    * Determine the type of an argument and return an appropriate object. If the type can't be determined return an
    * object of type {@link InvalidArgument}
    * @param arg  The argument you're looking for
    * @return     An object of a subclass of {@link Argument}
    */
  def apply(arg : String) : Argument = {
    if(arg.startsWith("-")) {
      supportedArgs.find{a => arg.contains(a.name) || a.synonyms.contains(arg.substring(1))} match {
        case None => new InvalidArgument(arg)
        case Some(argument) => argument
      }
    }
    else {
      new Value(arg) // should match as URL https://mathiasbynens.be/demo/url-regex
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
  override val toString = s"[$name]"
}

case class ParamArgument(override val name : String) extends Argument(name){
  var parameters : List[Argument] = Nil
  override def toString = s"-$name ${parameters.mkString(" ")}"
}

case class InvalidArgument(override val name : String) extends Argument(name){
  override def action = throw new UnsupportedOperationException(s"The Argument $name is not supported.")
}