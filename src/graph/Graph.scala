package graph

import scala.collection.mutable

/**
  * Every graph consist of nodes and edges.
  * This is an abstrat definition of a directed and unweighted graph (different weights can be given indirectly using edge labels)
  */
trait Graph[N <: Node[E] with Label, E <: Edge[N] with Label] {
  protected[graph] var nodes: Set[N] = Set[N]() //protected since every graph should be able to see its nodes
  protected[graph] var edges: Set[E] = Set[E]() //protected since every graph should be able to see its edges

  //exist only for test purpose
  def getNodes() : Set[N] = nodes
  def getEdges() : Set[E] = edges

  /**
    * Counts the number of nodes in this graph
    * @return number of nodes contained in this graph
    */
  def countNodes() : Int = {
    nodes.size
  }

  /**
    * Counts the number of edges contained in this graph
    * @return number of edges contained in this graph
    */
  def countEdges() : Int = {
    edges.size
  }

  /**
    * Adds a node to this graph (the graph is mutable)
    * @param node node to be added to the graph
    * @return this
    */
  protected def addNode(node : N) : Graph[N, E] = {
    nodes = nodes + node
    for(edge <- node.outgoingEdges; if !edges.contains(edge)) yield {
        addEdge(edge)
    }
    this
  }

  /**
    * Adds an directed edge to this graph (the graph is mutable)
    * @param edge edge to be added to the graph
    * @return this
    */
  protected def addEdge(edge : E) : Graph[N, E] = {
    if(!edges.exists((e) => e.startNode.equals(edge.startNode) && e.endNode.equals(edge.endNode)) ) //in case there is no edge with the same identity (there my be a difference in
      edges = edges + edge
    //add edges to nodes
    edge.startNode.addOutgoingEdge(edge)
    //add nodes of edge if not already existing
    if(!nodes.contains(edge.startNode))
      addNode(edge.startNode)
    if(!nodes.contains(edge.endNode))
      addNode(edge.endNode)
    this
  }

  /**
    * Removes a Node from the graph
    * @param node node to be removed frpm graph
    * @return this
    */
  protected def removeNode(node : N) : Graph[N, E] = {
    //remove all outgoing edges of node, then remove node
    for (edge <- node.outgoingEdges){
      removeEdge(edge)
    }
    nodes = nodes - node
    this
  }

  /**
    * Removes an Edge from the Graph
    * @param edge edge to be removed from graph
    * @return this
    */
  protected def removeEdge(edge : E) : Graph[N, E] = {
    //remove edge from startnode (it does not exist at endnode)
    edge.startNode.removeOutgoingEdge(edge)
    //now remove edge from graph (sipler with reliable equals but... it works
    edges = edges.filter((e) => if(edge.startNode.equals(e.startNode) && edge.endNode.equals(e.endNode)) false else true)
    //if there is't any edge containing the endnode as endnode the endnode isnt reachable anymore
    if(!edges.exists((e) => e.endNode.equals(edge.endNode)))
      removeNode(edge.endNode)

    this
  }

  /**
    * Depth first traversal on the graph (there is no constraint depth first traversal implemented yet)
    * @param node depth first traversal is starting with this node
    * @return List of nodes in order of the traversal
    */
  def depthFirstTraversal(node : N ) : List[N] = {
    setAllNodesUnvisited()
    node :: depthFirstTraversalHelper(node)
  }

  /**
    * The implementation of the recursive depth first traveral
    * @param node depth first traversal is starting with this node
    * @return List of nodes in order of the traversal
    */
  private def depthFirstTraversalHelper(node: N): List[N] = {
    var pagelist : List[N] = Nil
    node.visited = true
    for (child <- node.outgoingEdges.toList.map((e) => e.endNode)){
      if(!child.visited){
        pagelist = pagelist ::: child :: depthFirstTraversalHelper(child)
      }
    }
    pagelist
  }

  /**
    * Breadth first traversal on the graph
    * @param node breadth first traversal is starting with this node
    * @return List of nodes in order of the traversal
    */
  def breadthFirstTraversal(node : N ) : List[N] = {
    constraintBreadthFirstTraversal(node, (Edge) => true, (Node) =>true)
  }

  /**
    * Constraint  Breadth first search
    * @param node constraint breadth first traversal is starting with this node
    * @param edgeConstraint Function that maps Edges to Boolean to constrain paths
    * @param nodeConstraint Function that maps Nodes to Boolean to constrain node visits
    * @return List of nodes in order of the traversal
    */
  def constraintBreadthFirstTraversal(node: N, edgeConstraint: (E) => Boolean, nodeConstraint: (N) => Boolean): List[N] = {
    setAllNodesUnvisited()
    var pagelist : List[N] = Nil
    val queue : mutable.Queue[N] = mutable.Queue[N]()
    queue.enqueue(node)
    node.visited = true
    while(queue.nonEmpty){
      val tempnode = queue.dequeue()
      pagelist = tempnode :: pagelist
      for (child <- tempnode.outgoingEdges.toList.filter(edgeConstraint).map((e) => e.endNode)){
        if(!child.visited && nodeConstraint(child)){
          queue.enqueue(child)
          child.visited = true
        }
      }
    }
    pagelist.reverse
  }

  /**
    * Sets all nodes to unvisited and is needs to be called before any traversal
    */
  protected def setAllNodesUnvisited() : Graph[N, E] = {
    nodes.foreach(_.visited = false)
    this
  }

  /**
    * Analyzes every edge with a given function and adds the result to the label
    * @param analyzeFunction Analyze function
    */
  def analyzeEdges(analyzeFunction:(E) => LabelEntry) : Graph[N, E] ={
    edges.foreach(edge => edge.updateLabelEntry(analyzeFunction(edge)))
    this
  }

  /**
    * Analyzes every node with a given function and adds the result to the label
    * @param f f : (N) => Any function on node analyzing it
    * @return this
    */
  def analyzeNodes(f:(N) => LabelEntry) : Graph[N, E] = {
    for(node <- nodes){
      node.updateLabelEntry(f(node))
    }
    this
  }

  /**
    * Runs a simple Dijkstra and adds the label "dijkstra" to every node (every edge is weighted with 1)
    * @param node node where dijkstra starts
    * @return this
    */
  def runDijkstra(node : N) : Graph[N, E] = {
    runWeightedDijkstra(node, (e) => 1)
  }

  /**
    * Runs a constraint dijkstra and adds the labels "dijkstra" with distance and "parent" with a node to every node
    * @param node node where dijkstra starts
    * @param weightFunction function defining barriers for edges
    * @return this
    */
  def runWeightedDijkstra(node : N, weightFunction: (E) => Int): Graph[N, E] = { //TODO what happens if int is negative?
    val MaxInt = 32767
    var tempnodes = nodes.toList
    //for each node set distance to infinity
    analyzeNodes((node) => new LabelEntry("dijkstra", MaxInt))
    analyzeNodes((node) => new LabelEntry("parent", node))

    node.updateLabelEntry(new LabelEntry("dijkstra", 0))
    var tempnode : N = node

    while(tempnodes.nonEmpty){
      //select node with smallest distance from source
      tempnodes = tempnodes.sortWith((node1, node2) => node1.getLabelEntry("dijkstra").get.asInstanceOf[Int] < node2.getLabelEntry("dijkstra").get.asInstanceOf[Int])
      tempnode = tempnodes.head
      tempnodes = tempnodes.tail
      for(e <- tempnode.outgoingEdges){
        if(e.endNode.getLabelEntry("dijkstra").get.asInstanceOf[Int] > tempnode.getLabelEntry("dijkstra").get.asInstanceOf[Int] + weightFunction(e)){
          e.endNode.updateLabelEntry(new LabelEntry("dijkstra", tempnode.getLabelEntry("dijkstra").get.asInstanceOf[Int] + weightFunction(e)))
          e.endNode.updateLabelEntry(new LabelEntry("parent", tempnode ))
        }
      }
    }
    this
  }

}