package org.wmbus.protocol.utilities;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import yang.simulation.network.MasterGraphNode;

import java.util.ArrayList;
import java.util.List;

public class DGraph {


    public static SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge>  clone(SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> prev){
        SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> cloned = new SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        for (MasterGraphNode node:prev.vertexSet()) {
            cloned.addVertex(node);
        }
        for (DefaultWeightedEdge edge:prev.edgeSet()) {
            DefaultWeightedEdge e = cloned.addEdge(prev.getEdgeSource(edge),prev.getEdgeTarget(edge));
            cloned.setEdgeWeight(e,cloned.getEdgeWeight(edge));
        }
        return cloned;
    }

    public static ArrayList<Integer> getPath(Graph<MasterGraphNode, DefaultWeightedEdge> graph, List<DefaultWeightedEdge> edges){
        ArrayList<Integer> nodes = new ArrayList<Integer>();
        for (int i = 0; i < edges.size();i++){
            DefaultWeightedEdge e = edges.get(i);
            MasterGraphNode source = graph.getEdgeSource(e);
            MasterGraphNode destination = graph.getEdgeTarget(e);
            nodes.add(source.getStaticAddress());
            nodes.add(destination.getStaticAddress());
        }
        return nodes;
    }

}
