package org.wmbus.helpers;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.wmbus.simulation.WMBusSimulation;
import org.wmbus.simulation.WMbusSimulationConfig;
import org.wmbus.simulation.convergence.model.ConvergenceModel;
import org.wmbus.simulation.events.WMbusSimulationEventInterface;
import org.wmbus.simulation.stats.WMBusStats;

public class WMBusSimulationHelper {

    private static WMBusStats performSimulation(SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge > distanceGraph,
                                                WMbusSimulationConfig simulationConfig,
                                                ConvergenceModel simulationConvergence,
                                                WMbusSimulationEventInterface  events)  {

        WMBusSimulation simulation = new WMBusSimulation(distanceGraph,
                simulationConfig,
                simulationConvergence,
                events
        );
        simulation.run();
        return simulation.getResults();
    }
}
