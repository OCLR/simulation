package org.wmbus.protocol.utilities;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;


import java.util.ArrayList;
import java.util.List;

public class DGraph {


    public static SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge>  clone(SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> prev){
        SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> cloned = new SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        for (Integer node:prev.vertexSet()) {
            cloned.addVertex(node);
        }
        for (DefaultWeightedEdge edge:prev.edgeSet()) {
            DefaultWeightedEdge e = cloned.addEdge(prev.getEdgeSource(edge),prev.getEdgeTarget(edge));
            cloned.setEdgeWeight(e,cloned.getEdgeWeight(edge));
        }
        return cloned;
    }

    public static ArrayList<Integer> getPath(Graph<Integer, DefaultWeightedEdge> graph, List<DefaultWeightedEdge> edges){
        ArrayList<Integer> nodes = new ArrayList<Integer>();
        if (edges.size() !=0 ){
            Integer node = graph.getEdgeSource(edges.get(0));
            nodes.add(node);
        }
        for (int i = 0; i < edges.size();i++){
            DefaultWeightedEdge e = edges.get(i);
            Integer destination = graph.getEdgeTarget(e);
            //nodes.add(source);
            nodes.add(destination);
        }
        return nodes;
    }

}
