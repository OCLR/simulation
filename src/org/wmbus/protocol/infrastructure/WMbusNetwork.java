package org.wmbus.protocol.infrastructure;

import org.gfsk.GFSKModulation;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.pmw.tinylog.Logger;
import org.wmbus.protocol.nodes.Master;
import org.wmbus.protocol.nodes.Slave;
import org.wmbus.protocol.nodes.WMbusDevice;
import org.wmbus.protocol.simulation.WMBusSimulation;
import org.wmbus.protocol.utilities.DGraph;
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

    public Master getMaster() {
        return (Master) this.getNode(0);
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
        Master master = new Master(this.simulation,this.eccGraph);
        setNode(master, 0);
        //System.out.println("Network created");
    }

    private void generateNetwork(SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> distanceGraph) {
        int node = 1;
        for (MasterGraphNode n: distanceGraph.vertexSet()) {
            if (n.getStaticAddress() != 0){
                this.setNode(new Slave(this.simulation,node),node);
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
        double receiverPower = this.simulation.getwMbusConfig().CONF_TRASMITTER_POWER_LEVEL/(distance*distance);
        double signalToNoiseRatio = (receiverPower/ this.simulation.getwMbusConfig().CONF_NOISE_POWER);

        double ber = GFSKModulation.computeBer(signalToNoiseRatio, this.simulation.getwMbusConfig().CONF_GFSK_INDEX);
        Logger.info(ber+" "+signalToNoiseRatio);
        return ber;
    }
}
