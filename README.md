# Scala Webcrawler - Sitmap Generator

## Goal
1. Generating Sitamaps of random websites (Nico)
2. Multi-Threaded Structure (Nico)
3. Set depth to be crawled
4. control for redundant urls
5. context aware crawling (only inside of main and ```<class="content">``` tags)
6. Information retrival regarding urls: Sitemap with Coleman-Liau or Flesch–Kincaid readability tests (David)
7. robot exclusion standart beachten
4. https://en.wikipedia.org/wiki/Web_crawler#/media/File:WebCrawlerArchitecture.svg

## Scala Features
1. Option Monaden / Futures
2. Filter list of Results 
3. Decorator Pattern

## Project Plan
1. Commandline - Tool for HTTP requests argumente (David)
    * ```scrawl url [url, ...]```
    *```-analyze | -a Coleman-Liau .... Aritkellängen....```
    *```-L | -Level ```
2. output xml file (generische Ausgabe vorsehen für weitere Dateitypen)