package main

import java.net.{MalformedURLException, URL}
import java.nio.file.Files

import cli._
import crawling.{CrawlPrefs, CollectorSystem}
import webgraph.Webgraph

import scala.xml.PrettyPrinter

/**
  * Command line tool to crawl websites. Generates customizable site maps and saves them as XML files on the disk.
  * For a list of the supported arguments see [[cli.Argument]] or call the program with the parameter `-help`. Site map
  * customization can also be defined there.
  */
object Scrawl extends CommandLineInterpreter{
  def main(args : Array[String]) = {
    try{
      val arguments = interpretArguments(args)
      val websites = onlyWebsites(arguments)
      if(websites.isEmpty) throw new CommandLineException("Enter at least one website to crawl.")
      println(s"Crawling the sites [${websites.mkString(", ")}] with the arguments [${arguments.mkString(", ")}]")
      websites.foreach(website => {
        val graph = CollectorSystem.crawlPage(new URL(prepareUrl(website.toString)))
        saveGraphToFile(graph)
      })
      sys.exit()
    } catch {
      case e : Exception => exit(e.getMessage)
      case e : MalformedURLException => exit(s"Invalid URL: ${e.getCause}")
    }
  }

  /**
    * Saves a graph to file. The location of the file is specified in the [[crawling.CrawlPrefs]]. The filename is
    * derived from the crawled page.
    * @param graph  The graph to save
    */
  private def saveGraphToFile(graph : Webgraph): Unit ={
    val printer = new PrettyPrinter(300, 2)
    val content = printer.format(graph.xml).getBytes()
    val filename = graph.root.url.getHost.replace("[.\\]", "_") + ".xml"
    val file = CrawlPrefs.outDir.toFile
    file.mkdirs()
    val resultingPath = new java.io.File(file, filename)
    Files.write(resultingPath.toPath, content)
    println(s"File saved to ${resultingPath.getAbsolutePath}.")
  }

  /**
    * Helper function to filter a list of arguments to only contain websites. It is assumed that websites exist in the
    * Form of [[cli.Value]]s.
    *
    * @param args The list of arguments to filter
    * @return     The filtered List
    */
  private def onlyWebsites(args : List[Argument]) = {
    args.filter(_.isInstanceOf[Value])
  }

  /**
    * Helper function to prepend a URL with `http://` if applicable.
    *
    * @param website  The website string to prepare
    * @return         A new URL that should be instantiable with [[java.net.URL]]
    */
  private def prepareUrl(website : String) : String ={
    if(!website.startsWith("http")){
      "http://" + website
    } else{
      website
    }
  }

  /**
    * Exits the program in case of an error and prints the provided message. Also prints a usage hint.
    *
    * @param message  The message to print
    */
  private def exit(message : String) : Unit = {
    println(message)
    println()
    println("Use -help for information on how to use the program.")
    println("Exiting...")
    sys.exit(1)
  }
}
