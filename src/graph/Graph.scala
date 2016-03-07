package graph

import scala.collection.mutable

/**
  * Created by nicohein on 29/02/16.
  */
trait Graph[N <: Node[E] with Label, E <: Edge[N] with Label] {
  protected var nodes: Set[N] = Set[N]() //protected since every graph should be able to see its nodes
  protected var edges: Set[E] = Set[E]() //protected since every graph should be able to see its edges

  /**
    *
    * @return number of nodes contained in this graph
    */
  @deprecated("Counts nodes but is ambiguous")
  def count() : Int = {
    countNodes()
  }

  /**
    *
    * @return number of nodes contained in this graph
    */
  def countNodes() : Int = {
    nodes.size
  }

  /**
    *
    * @return number of edges contained in this graph
    */
  def countEdges() : Int = {
    edges.size
  }

  /**
    *
    * @param node node to be added to the graph
    */
  protected def addNode(node : N) : Graph[N, E] = {
    nodes = nodes + node
    for(edge <- node.edges){  //not really beatuiful
      if(!edges.contains(edge))
        addEdge(edge)
    }
    this
  }

  /**
    *
    * @param edge edge to be added to the graph
    */
  protected def addEdge(edge : E) : Graph[N, E] = {
    edges = edges + edge
    //add edges to nodes
    edge.startNode.addEdge(edge)
    edge.endNode.addEdge(edge)
    //add nodes of edge if not already existing
    if(!nodes.contains(edge.startNode))
      addNode(edge.startNode)
    if(!nodes.contains(edge.endNode))
      addNode(edge.endNode)
    this
  }

  /**
    *
    * @param node node to be removed frpm graph
    */
  protected def removeNode(node : N) : Graph[N, E] = {
    //remove all edges of node, then remove node
    for(edge <- node.edges){
      removeEdge(edge)
    }
    nodes = nodes - node
    this
  }

  /**
    *
    * @param edge edge to be removed from graph
    */
  protected def removeEdge(edge : E) : Graph[N, E] = {
    //it is not necessary to remove the edges from all nodes
    edge.startNode.removeEdge(edge)
    edge.endNode.removeEdge(edge)
    if(edge.startNode.edges.isEmpty)
      removeNode(edge.startNode)
    if(edge.endNode.edges.isEmpty)
      removeNode(edge.endNode)
    //now remove edge from graph
    edges = edges - edge
    this
  }

  /**
    *
    * @param node depth first traversal is starting with this node
    * @return List of nodes in order of the traversal
    */
  def depthFirstTraversal(node : N ) : List[N] = {
    setUnvisited()
    node :: depthFirstTraversalHelper(node)
  }

  private def depthFirstTraversalHelper(node: N): List[N] = {
    var pagelist : List[N] = Nil
    node.visited = true
    for(child <- node.edges.toList.map((e) => e.endNode)){
      if(!child.visited){
        pagelist = pagelist ::: child :: depthFirstTraversalHelper(child)
      }
    }
    pagelist
  }

  /**
    *
    * @param node breadth first traversal is starting with this node
    * @return List of nodes in order of the traversal
    */
  def breadthFirstTraversal(node : N ) : List[N] = {
    contraintBreadthFirstTraversal(node, (Edge) => true, (Node) =>true)
  }

  /**
    *
    * @param node constraint breadth first traversal is starting with this node
    * @param f Function that maps Edges to Boolean to constrain paths
    * @param g Function that maps Nodes to Boolean to constrain node visits
    * @return
    */
  def contraintBreadthFirstTraversal(node: N, f: (E) => Boolean, g: (N) => Boolean): List[N] = {
    setUnvisited()
    var pagelist : List[N] = Nil
    val queue : mutable.Queue[N] = mutable.Queue[N]()
    queue.enqueue(node)
    node.visited = true

    while(queue.nonEmpty){
      val tempnode = queue.dequeue()
      pagelist = tempnode :: pagelist
      for(child <- tempnode.edges.toList.filter(f).map((e) => e.endNode)){
        if(!child.visited && g(child)){
          queue.enqueue(child)
          child.visited = true
        }
      }
    }
    pagelist.reverse
  }

  /**
    * is called before an breadth or depth first traversal
    */
  protected def setUnvisited() : Graph[N, E] = {
    for(node <- nodes){
      node.visited = false
    }
    this
  }

  /**
    *
    * @param labelkey The key referencing the results in labels
    * @param f f : (E) => Any function on edge analyzing it
    */
  def analyzeEdges(labelkey : String, f:(E) => Any) : Graph[N, E] ={
    for(edge <- edges){
      edge.updateLabelEntry(new LabelEntry(labelkey, f(edge)))//map?
    }
    this
  }

  /**
    *
    * @param labelkey  The key referencing the results in labels
    * @param f f : (N) => Any function on node analyzing it
    * @return this
    */
  def analyzeNodes(labelkey : String, f:(N) => Any) : Graph[N, E] = {
    for(node <- nodes){
      node.updateLabelEntry(new LabelEntry(labelkey, f(node)))  //map?
    }
    this
  }

  /**
    *
    * @param node node where dijkstra starts
    * @return this
    */
  def dijkstra(node : N) : Graph[N, E] = {
    constraintDijkstra(node, (e) => true, (n) => true)
  }
  def constraintDijkstra(node : N, f: (E) => Boolean, g: (N) => Boolean ): Graph[N, E] = {
    val MaxInt = 32767
    var tempnodes = nodes.toList.filter(g)
    //for each node set distance to infinity
    analyzeNodes("dijkstra", (n : N) => MaxInt)

    node.updateLabelEntry(new LabelEntry("dijkstra", 0))
    var tempnode : N = node

    while(tempnodes.nonEmpty){
      //select node with smallest distance from source
      tempnodes = tempnodes.sortWith((node1, node2) => node1.getLabelEntry("dijkstra").asInstanceOf[Int] < node2.getLabelEntry("dijkstra").asInstanceOf[Int])
      tempnode = tempnodes.head
      tempnodes = tempnodes.tail
      for(n <- tempnode.edges.toList.filter(f).map((e) => e.endNode)){
        if( n.getLabelEntry("dijkstra").asInstanceOf[Int] > tempnode.getLabelEntry("dijkstra").asInstanceOf[Int]+1 ){
          n.updateLabelEntry(new LabelEntry("dijkstra", tempnode.getLabelEntry("dijkstra").asInstanceOf[Int]+1))
        }
      }
    }
    this
  }

}