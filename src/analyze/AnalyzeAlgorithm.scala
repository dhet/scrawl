package analyze

import abstractGraph.LabelEntry
import webgraph1.Webpage

/**
  * Created by nicohein on 06/03/16.
  */
abstract class AnalyzeAlgorithm {
  def analyze(webpage: Webpage) : LabelEntry
}

object WordCount extends AnalyzeAlgorithm{
  override def analyze(webpage: Webpage): LabelEntry = {
    new LabelEntry("wordcount", webpage.content.split(" ").map((string) => 1).sum)
  }
}
