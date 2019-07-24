package org.wmbus.protocol.simulation;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.wmbus.protocol.infrastructure.WMbusNetwork;
import yang.simulation.network.MasterGraphNode;

public class WMBusSimulation {

    private WMbusNetwork wMbusNetwork;
    private WMbusConfig wMbusConfig;
    private org.wmbus.protocol.simulation.WMBusStats WMBusStats;

    public WMBusSimulation(SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> distanceGraph, boolean withHammingAlgorithm, boolean withWakeUpAlgorithm , long lasting) {
        this.wMbusConfig = new WMbusConfig(withHammingAlgorithm,withWakeUpAlgorithm,lasting);
        this.WMBusStats = new WMBusStats();
        this.wMbusNetwork = new WMbusNetwork(this,distanceGraph);
    }

    public WMbusNetwork getwMbusNetwork() {
        return wMbusNetwork;
    }

    public WMBusStats getResults() {
        return WMBusStats;
    }

    public WMbusConfig getwMbusConfig() {
        return wMbusConfig;
    }


    public void run()  {
        this.wMbusNetwork.getMaster().lifeCycle();
    }
}
