# Scrawl
Scrawl is a highly parallel and distributed webscraper written in Scala. It is designed as a CLI which takes a base URI of a website as input and then scrapes that website, as well as all of its children, recursively. Scrawl aims to achieve a high level of concurrency. For this, akka's actor system is employed: Each web document is scraped by a separate actor, which in turn spawns a number of other actors for each link it encounters while scraping.

This project was created by David Hettler and Nico Hein as part of a semester project at LMU Munich.

The documentation is written in German and can be found [here](README-deutsch.md). 

## Usage
```sh
$ scrawl URL [PARAMETERS]
```
**Parameters:**

* `-level <level>`, synonyms: `-l, -d, -depth`
  * Determines how many levels to crawl. Expects one argument of type Integer. 
* `-out`, synonyms: `-o, -path, -dir`
  * Specifies the directory where to save the result. The files are saved in the ./sitemaps directory by default. The name of the xml files correspond to the crawled sites
* `-help`, synonyms: `-h, -?, -wat`
  * Prints a help text


