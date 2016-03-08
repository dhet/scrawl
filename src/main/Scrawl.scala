package main

import java.net.{MalformedURLException, URL}

import cli._
import crawling.CollectorSystem

object Scrawl {
  def main(args : Array[String]) = {
    var websites : List[String] = Nil
    var activeArguments : List[Argument] = Nil
    if(args.isEmpty){
      Argument.printHelp
      exit(s"Enter at least one website to crawl.\n")
    } else {
      for (arg <- args; argument = Argument(arg)) {
        argument match {
          case Flag(_) => activeArguments :+= argument
          case ParamArgument(_) => activeArguments :+= argument
          case InvalidArgument(_) => exit(s"Unsupported Command $arg.")
          case Value(_) =>
            if(activeArguments.nonEmpty && activeArguments.last.isInstanceOf[ParamArgument]){
              activeArguments.last.asInstanceOf[ParamArgument].parameter = argument
            } else{
              websites :+= arg
            }
        }
      }
      applyArguments(activeArguments)
      println(s"Crawling the sites [${websites.mkString(", ")}] with the arguments [${activeArguments.mkString(", ")}]")
      websites.foreach(website => {
        try{
          CollectorSystem.crawlPage(new URL(prepareUrl(website)))
        } catch {
          case e : MalformedURLException => exit(s"Invalid URL: $website")
        }
      })
    }
  }

  def prepareUrl(website : String) : String ={
    if(!website.startsWith("http")){
      "http://" + website
    } else{
      website
    }
  }

  def applyArguments(args : List[Argument]) = {
    for(arg <- args){
      try{
        arg.action
      } catch {
        case e : Exception => exit(s"""Wrong usage of argument "$arg".""")
      }
    }
  }

  def exit(message : String) = {
    println(message)
    println()
    println("Exiting...")
    sys.exit(1)
  }
}
