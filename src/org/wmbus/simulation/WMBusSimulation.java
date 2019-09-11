package org.wmbus.simulation;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.wmbus.protocol.infrastructure.WMBusNoise;
import org.wmbus.protocol.infrastructure.WMbusNetwork;
import org.wmbus.simulation.convergence.model.ConvergenceModel;
import org.wmbus.simulation.events.WMbusSimulationEventInterface;
import org.wmbus.simulation.stats.WMBusStats;

public class WMBusSimulation {

    private final ConvergenceModel wMbusSimulationConvergence;
    private WMbusNetwork wMbusNetwork;
    private WMbusSimulationConfig wMbusSimulationConfig;
    private WMBusNoise WMBusNoise;
    private org.wmbus.simulation.stats.WMBusStats WMBusStats;
    private WMbusSimulationEventInterface events;


    public WMBusSimulation(SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> distanceGraph, WMbusSimulationConfig simulationConfig, ConvergenceModel simulationConvergence, WMbusSimulationEventInterface events) {
        this.wMbusSimulationConvergence = simulationConvergence;
        this.wMbusSimulationConfig = simulationConfig;
        this.events = events;
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

    public ConvergenceModel getwMbusSimulationConvergence() {
        return wMbusSimulationConvergence;
    }

    public WMbusSimulationEventInterface getWMbusEvents() {
        return events;
    }
}
