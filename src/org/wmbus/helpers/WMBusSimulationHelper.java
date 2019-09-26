package org.wmbus.helpers;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.wmbus.protocol.config.WMBusDeviceConfig;
import org.wmbus.simulation.WMBusSimulation;
import org.wmbus.simulation.WMbusSimulationConfig;
import org.wmbus.simulation.convergence.model.ConvergenceModel;
import org.wmbus.simulation.events.WMbusSimulationEventInterface;
import org.wmbus.simulation.stats.WMBusStats;

public class WMBusSimulationHelper {

    private static WMBusStats performSimulation(SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge > distanceGraph,
                                                WMbusSimulationConfig simulationConfig,
                                                WMBusDeviceConfig deviceConfig,
                                                ConvergenceModel simulationConvergence,
                                                WMbusSimulationEventInterface  events)  {

        WMBusSimulation simulation = new WMBusSimulation(distanceGraph,
                deviceConfig,
                simulationConfig,
                simulationConvergence,
                events
                );
        simulation.run();
        return simulation.getResults();
    }
}
