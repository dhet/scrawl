package analyze

import graph.LabelEntry
import webgraph.Webpage

/**
  * Created by nicohein on 06/03/16.
  */
@deprecated
trait AnalyzeAlgorithm {
  def analyze(webpage: Webpage) : LabelEntry
}

@deprecated
object WordCount extends AnalyzeAlgorithm{
  override def analyze(webpage: Webpage): LabelEntry = {
    new LabelEntry("wordcount", webpage.content.split(" ").map((string) => 1).sum)
  }
}
