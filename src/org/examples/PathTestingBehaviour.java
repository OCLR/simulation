package org.examples;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.wmbus.protocol.utilities.DGraph;
import yang.nodes.Neighbor;
import yang.nodes.Node;
import yang.nodes.SpaceConnectedNode;

import java.util.ArrayList;

public class PathTestingBehaviour {
    public static void main(String[] args) {
        // Same weight
        SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> network = new SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        ArrayList<Node> nodes = new ArrayList<Node>();
        ArrayList<Neighbor> neighbors = new ArrayList<Neighbor>();
        DefaultWeightedEdge edge;

        nodes.add(new SpaceConnectedNode(0, 10, 10, neighbors));
        nodes.add(new SpaceConnectedNode(1, 10, 10, neighbors));
        nodes.add(new SpaceConnectedNode(2, 10, 10, neighbors));
        nodes.add(new SpaceConnectedNode(3, 10, 10, neighbors));
        nodes.add(new SpaceConnectedNode(4, 10, 10, neighbors));
        nodes.add(new SpaceConnectedNode(5, 10, 10, neighbors)); // 5 my destination.
        network.addVertex(0);
        network.addVertex(1);
        network.addVertex(2);
        network.addVertex(3);
        network.addVertex(4);
        network.addVertex(5);
        // 0->1->5
        // 0->1->2->3->4->5
        edge = network.addEdge(0, 1);
        network.setEdgeWeight(edge, 0);
        edge = network.addEdge(1, 2);
        network.setEdgeWeight(edge, 0);
        edge = network.addEdge(1, 5);
        network.setEdgeWeight(edge, 0);
        edge = network.addEdge(2, 3);
        network.setEdgeWeight(edge, 0);
        edge = network.addEdge(3, 4);
        network.setEdgeWeight(edge, 0);
        edge = network.addEdge(4, 5);
        network.setEdgeWeight(edge, 0);
        //
        // Compute the shortest path.
        // Expected one.
        // 0->1->5
        DijkstraShortestPath dijkstraPaths = new DijkstraShortestPath<Integer, DefaultWeightedEdge>(network);
        GraphPath p = dijkstraPaths.getPath(0, 5);
        assert (p!=null);
        ArrayList<Integer> list = new ArrayList<Integer>(DGraph.getPath(network, p.getEdgeList()));
        assert list.size() == 3;
        assert list.get(0) == 0;
        assert list.get(1) == 1;
        assert list.get(2) == 5;
        // let's add a new path.
        edge = network.addEdge(0, 2);
        network.setEdgeWeight(edge, 0);
        edge = network.addEdge(2, 5);
        network.setEdgeWeight(edge, 0);
        p = dijkstraPaths.getPath(0, 5);
        list = new ArrayList<Integer>(DGraph.getPath(network, p.getEdgeList()));
        assert list.size() == 3;
        assert list.get(0) == 0;
        assert list.get(1) == 1;
        assert list.get(2) == 5;
        // let's see the shortest.
        edge = network.addEdge(0, 5);
        network.setEdgeWeight(edge, 0);
        p = dijkstraPaths.getPath(0, 5);
        list = new ArrayList<Integer>(DGraph.getPath(network, p.getEdgeList()));
        assert list.size() == 2;
        assert list.get(0) == 0;
        assert list.get(1) == 5;
        // What i get, is that it takes the shortest one.
    }

}

