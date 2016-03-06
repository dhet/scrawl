package abstractgraph

/**
  * Created by nicohein on 06/03/16.
  */
trait Label{
  protected[abstractgraph] var label = Set[LabelEntry]()
}