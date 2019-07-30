package org.wmbus.simulation;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.wmbus.protocol.infrastructure.WMBusNoise;
import org.wmbus.protocol.infrastructure.WMbusNetwork;
import org.wmbus.simulation.stats.WMBusStats;
import yang.simulation.network.MasterGraphNode;

public class WMBusSimulation {

    private WMbusNetwork wMbusNetwork;
    private WMbusSimulationConfig wMbusSimulationConfig;
    private WMBusNoise WMBusNoise;
    private org.wmbus.simulation.stats.WMBusStats WMBusStats;

    public WMBusSimulation(SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> distanceGraph, boolean withHammingAlgorithm, boolean withWakeUpAlgorithm ) {
        this.wMbusSimulationConfig = new WMbusSimulationConfig(withHammingAlgorithm,withWakeUpAlgorithm);
        this.WMBusStats = new WMBusStats();
        this.wMbusNetwork = new WMbusNetwork(this,distanceGraph);
        this.WMBusNoise = new WMBusNoise(this);
    }

    public WMbusNetwork getwMbusNetwork() {
        return wMbusNetwork;
    }
    public WMbusNetwork get() {
        return wMbusNetwork;
    }

    public WMBusStats getResults() {
        return WMBusStats;
    }

    public WMbusSimulationConfig getwMbusSimulationConfig() {
        return wMbusSimulationConfig;
    }
    public WMBusNoise getWMBusNoise() {
        return WMBusNoise;
    }


    public void run()  {
        this.wMbusNetwork.getMaster().lifeCycle();
    }
}
