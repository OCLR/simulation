package org.wmbus.protocol.infrastructure;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.wmbus.protocol.nodes.WMBusMaster;
import org.wmbus.protocol.nodes.WMBusSlave;
import org.wmbus.protocol.nodes.WMbusDevice;
import org.wmbus.protocol.utilities.DGraph;
import org.wmbus.simulation.WMBusSimulation;
import yang.simulation.network.MasterGraphNode;

import java.util.HashMap;
import java.util.Set;

public class WMbusNetwork {

    private HashMap<Integer,WMbusDevice> nodes = new HashMap<Integer, WMbusDevice>();
    private SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> eccGraph;
    private SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> distanceGraph;
    private WMBusSimulation simulation;


    public WMbusNetwork(WMBusSimulation simulation,SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> distanceGraph)  {
        //super(owner, name, showInReport, showInTrace);
        this.simulation = simulation;
        this.distanceGraph = distanceGraph;
        this.create();
    }

    public HashMap<Integer,WMbusDevice>  getNodes() {
        return nodes;
    }

    public WMBusMaster getMaster() {
        return (WMBusMaster) this.getNode(0);
    }

    protected void setNode(WMbusDevice node, int pos) {
        this.nodes.put(pos,node);
    }

    public WMbusDevice getNode(int pos) {
        return this.nodes.get(pos);
    }

    public synchronized Set<DefaultWeightedEdge> getOutgoingEdges(int source) {
        return this.eccGraph.edgesOf(new MasterGraphNode(source));
    }

    private void create(){
        this.eccGraph =  DGraph.clone(this.distanceGraph);
        this.eccGraph = this.resetNetwork(this.eccGraph);
        this.generateNetwork(this.distanceGraph);
        WMBusMaster wmbusMaster = new WMBusMaster(this.simulation,this.eccGraph);
        setNode(wmbusMaster, 0);
        //System.out.println("Network created");
    }

    private void generateNetwork(SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> distanceGraph) {
        int node = 1;
        for (MasterGraphNode n: distanceGraph.vertexSet()) {
            if (n.getStaticAddress() != 0){
                this.setNode(new WMBusSlave(this.simulation,node),node);
                node++;
            }

        }
    }

    private SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> resetNetwork(SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> networkGraph){
        SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> masterGraph = DGraph.clone(networkGraph);
        Set<DefaultWeightedEdge> edges =  masterGraph.edgeSet();
        //
        for (DefaultWeightedEdge edge: edges){
            masterGraph.setEdgeWeight(edge,ECC.RESET);
        }
        return masterGraph;
    }

    public SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> getEccGraph() {
        return this.eccGraph;
    }

    public double getDistance(int source,int destination){
        DefaultWeightedEdge ed = this.distanceGraph.getEdge(new MasterGraphNode(source),new MasterGraphNode(destination));
        Double distance = this.distanceGraph.getEdgeWeight(ed);
        return distance;
    }
    public double getBer(int source, int destination) {
        // get distance from graph.
        // compute ber from distance
        // return ber.
        DefaultWeightedEdge ed = this.distanceGraph.getEdge(new MasterGraphNode(source),new MasterGraphNode(destination));
        Double distance = this.distanceGraph.getEdgeWeight(ed);
        // Here we have the noise level.
        if (distance == 0){
            throw new IllegalArgumentException("Sorry it's not a good day... Distance can't be zero.");
        }

        return this.simulation.getWMBusNoise().getBerFromDistance(distance);
    }

    public SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> getDistanceGraph() {
        return this.distanceGraph;
    }
}
