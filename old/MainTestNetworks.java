package org.wmbus;

import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.wmbus.simulation.ResultTable;
import org.wmbus.simulation.WMBusSimulation;
import org.wmbus.simulation.stats.WMBusStats;
import yang.NetworkGeneratorHelper;
import yang.simulation.network.SimulationNetworkWithDistance;

import java.util.ArrayList;

public class MainTestNetworks {

    public static void main(String[] args) throws Exception {
        Configurator.currentConfig()
                .formatPattern("{level}: {class}.{method}()\t{message}")
                .level(Level.OFF)
                .activate();
        for (int nodes = 3; nodes < 15; nodes++) {
            // Integer.parseInt(args[0]),Boolean.parseBoolean(args[1]),Boolean.parseBoolean(args[2])
            System.out.println("Nodes network: "+nodes);
            LocalDateTime now = LocalDateTime.now();
            ResultTable res = MainTestNetworks.convergesResult(nodes, true,true );
            System.out.println(res.prettyPrint());
           // System.out.println(res.printValues(false));
            LocalDateTime after = LocalDateTime.now();
            Period period = new Period(now, after);
            long minutes = period.getMinutes();
            System.out.println("Time: "+minutes+" min and "+ period.getSeconds() % 60+ " seconds");
        }

    }

    /**
     *
     * @param nNodes
     * @return Last convergence network.
     */
    public static ResultTable convergesResult(int nNodes, boolean withHamming, boolean withWakeup){
        ArrayList<SimulationNetworkWithDistance> attempts = new ArrayList<SimulationNetworkWithDistance>();
        boolean convergence = false;
        int convergenceTime = 0;
        double convergencePerc = 0.0;
        double result = 0, resultsum = 0;
        double index = 0;
        WMBusStats simulationResults = null;
        do {
            SimulationNetworkWithDistance attempt = NetworkGeneratorHelper.generateInterconnectedRadiusNetwork(
                    nNodes,
                    GlobalConfiguration.NETWORK_SIZE_METERS,
                    GlobalConfiguration.MIN_NODE_RADIUS,
                    GlobalConfiguration.MAX_NODE_RADIUS,
                    GlobalConfiguration.MASTER_X,
                    GlobalConfiguration.MASTER_Y,
                    -1);
            // Add attempt.
            //sint numberOfAttempt = attempts.size();

            //int numberOfAttemptAfter = attempts.size();

            // Check number of attempt.
            if (attempts.indexOf(attempt) == -1) {
                attempts.add(attempt);
                // Perform simulation.
                simulationResults = MainTestNetworks.performSimulation(attempt, withHamming, withWakeup);
                result = (simulationResults.masterTrasmissionFaultWithUpdate + simulationResults.masterTrasmissionFaultWithNoUpdate)/simulationResults.masterSentMessage;

                double average;
                resultsum+=result;
                index++;
                average = resultsum/index;
                convergencePerc = ((average)*GlobalConfiguration.CONVERGENCE_CONFIDENCE_PERCENTAGE)/100;

                boolean tempConvergence = ( (average-result) <= convergencePerc);
                if (tempConvergence){
                    convergenceTime++;
                }else{
                    convergenceTime = 0;
                }
                System.out.println("Network convergence "+average+ "  Percentage: "+ convergencePerc + " Convergence:"+convergenceTime);
                //
                if (convergenceTime == GlobalConfiguration.CONVERGENCE_CONFIDENCE_TIMES){
                    convergence = true;
                }
            }else{
                System.out.println("Yeah");
            }
        }while(!convergence);
        return simulationResults.printResults();
    }

    private static WMBusStats performSimulation(SimulationNetworkWithDistance simulationWrapper, boolean withHamming, boolean withWakeup)  {

        WMBusSimulation simulation = new WMBusSimulation(simulationWrapper.network,withHamming,withWakeup, events);
        simulation.run();
        return simulation.getResults();
    }
}
