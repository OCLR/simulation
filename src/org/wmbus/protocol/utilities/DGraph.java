package org.wmbus.protocol.utilities;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import yang.simulation.network.MasterGraphNode;

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
}
