package webgraph


import graph.{LabelEntry, Label}

import scala.collection.mutable

/**
  * The Weblabel is a concrete implemetation of a Label (Which is used in all edges (Weblinks) ans nodes (Webpages)
  * In addition to label trait it supports xml output
  * It takes advantage of the default LabelEntry
  */
trait Weblabel extends Label{

  override val label = mutable.Set[LabelEntry]()

  /**
    * Returns the resurlt of the analyzes in a plane structure
    * @return xml
    */
  def labelxml =
    <labels>
    {for (labelentry <- label) yield
        <label key={labelentry.key.toString} value={labelentry.value.toString}/>
    }
    </labels>

}
