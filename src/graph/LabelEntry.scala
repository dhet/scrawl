package graph

/**
  * Every label (mixed into all nodes and edges) consists of a list of label entries.
  * Every label entry consists of an key (String) value (Any) pair
  */
case class LabelEntry(key : String, value : Any)
