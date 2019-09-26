package org.wmbus.protocol.infrastructure;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.wmbus.protocol.nodes.WMBusMaster;
import org.wmbus.protocol.nodes.WMBusSlave;
import org.wmbus.protocol.nodes.WMbusDevice;
import org.wmbus.protocol.utilities.DGraph;
import org.wmbus.simulation.WMBusSimulation;

import java.util.HashMap;
import java.util.Set;

public class WMbusNetwork {

    private HashMap<Integer,WMbusDevice> nodes = new HashMap<Integer, WMbusDevice>();
    private SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> eccGraph;
    private SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> distanceGraph;
    private WMBusSimulation simulation;


    public WMbusNetwork(WMBusSimulation simulation,SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> distanceGraph)  {
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
        return this.eccGraph.edgesOf((source));
    }

    private void create(){
        this.eccGraph =  DGraph.clone(this.distanceGraph);
        this.eccGraph = this.resetNetwork(this.eccGraph);
        this.generateNetwork(this.distanceGraph);
        WMBusMaster wmbusMaster = new WMBusMaster(this.simulation,this.eccGraph);
        setNode(wmbusMaster, 0);
        //System.out.println("Network created");
    }

    private void generateNetwork(SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> distanceGraph) {
        int node = 1;
        for (Integer n: distanceGraph.vertexSet()) {
            if (n != 0){
                this.setNode(new WMBusSlave(this.simulation,node),node);
                node++;
            }

        }
    }

    private SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> resetNetwork(SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> networkGraph){
        SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> masterGraph = DGraph.clone(networkGraph);
        Set<DefaultWeightedEdge> edges =  masterGraph.edgeSet();
        //
        for (DefaultWeightedEdge edge: edges){
            masterGraph.setEdgeWeight(edge,ECC.RESET);
        }
        return masterGraph;
    }

    public SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> getEccGraph() {
        return this.eccGraph;
    }

    public double getDistance(int source,int destination){
        DefaultWeightedEdge ed = this.distanceGraph.getEdge((source),(destination));
        Double distance = this.distanceGraph.getEdgeWeight(ed);
        return distance;
    }
    public double getBer(int source, int destination) throws Exception {
        // get distance from graph.
        // compute ber from distance
        // return ber.
        DefaultWeightedEdge ed = this.distanceGraph.getEdge((source),(destination));
        Double distance = this.distanceGraph.getEdgeWeight(ed);
        // Here we have the noise level.
        if (distance == 0){
            throw new IllegalArgumentException("Sorry it's not a good day... Distance can't be zero.");
        }

        return this.simulation.getWMBusNoise().getBerFromDistance(distance);
    }

    public SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> getDistanceGraph() {
        return this.distanceGraph;
    }
}
