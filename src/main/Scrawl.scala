package main

import cli._

object Scrawl {
  def main(args : Array[String]) = {
    var websites : List[String] = Nil
    var activeArguments : List[Argument] = Nil
    if(args.isEmpty){
      exit(s"Enter at least one website to crawl.\n")
    } else {
      for (arg <- args; argument = Argument(arg)) {
        argument match {
          case Flag(_) => activeArguments :+= argument
          case ParamArgument(_) => activeArguments :+= argument
          case InvalidArgument(_) => Argument.printHelp; exit(s"Unsupported Command $arg.")
          case Value(_) => //isn't this unsafe?
            if(activeArguments.nonEmpty && activeArguments.last.isInstanceOf[ParamArgument]){
              activeArguments.last.asInstanceOf[ParamArgument].parameters :+= argument
            }else{
              websites :+= arg
            }
        }
      }
      println(s"Crawl the sites [${websites.mkString(", ")}] with the arguments [${activeArguments.mkString(", ")}]")
      activeArguments.foreach(arg => arg.action)
    }
  }

  def exit(message : String) = {
    println(message)
    println("Exiting...")
    sys.exit(1)
  }
}
