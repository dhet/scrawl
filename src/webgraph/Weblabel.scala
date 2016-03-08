package webgraph


import graph.{LabelEntry, Label}

import scala.collection.mutable

/**
  * Created by nicohein on 01/03/16.
  */


trait Weblabel extends Label{

  override val label : mutable.Set[LabelEntry] = mutable.Set[LabelEntry]()

  def labelxml =
    <labels>
    {for (labelentry <- label) yield
        <label key={labelentry.key.toString} value={labelentry.value.toString}/>
    }
    </labels>

}
