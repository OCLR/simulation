package org.wmbus;

import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.wmbus.simulation.WMBusSimulation;
import org.wmbus.simulation.stats.WMBusStats;
import yang.NetworkGeneratorHelper;
import yang.simulation.network.SimulationNetworkWithDistance;

import java.util.ArrayList;

public class MainTestNetwork {

    public static void main(String[] args) throws Exception {
        Configurator.currentConfig()
                .formatPattern("{level}: {class}.{method}()\t{message}")
                .level(Level.INFO)
                .activate();

        for (int i = 3; i < 10; i++) {
            MainTestNetwork.convergesResult(4,true,true);
        }

    }

    /**
     *
     * @param nNodes
     * @return Last convergence network.
     */
    public static void convergesResult(int nNodes, boolean withHamming, boolean withWakeup){
        ArrayList<SimulationNetworkWithDistance> attempts = new ArrayList<SimulationNetworkWithDistance>();
        boolean convergence = false;
        int convergenceTime = 0;
        double convergencePerc = 0.0;
        double result = 0, sum = 0;
        double average = 0;
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
            int numberOfAttempt = attempts.size();
            attempts.add(attempt);
            int numberOfAttemptAfter = attempts.size();

            // Check number of attempt.
            if (numberOfAttemptAfter != numberOfAttempt) {
                // Perform simulation.
                simulationResults = MainTestNetwork.performSimulation(attempt, withHamming, withWakeup);
                result = simulationResults.masterTrasmissionFaultWithUpdate + simulationResults.masterTrasmissionFaultWithNoUpdate;

                sum += result;
                index++;
                average = sum/index;

                convergencePerc = ((average)*GlobalConfiguration.CONVERGENCE_CONFIDENCE_PERCENTAGE)/100;
                /*
                    Convergence algorithm.
                 */
                boolean tempConvergence = ( (average - result) <= convergencePerc);
                if (tempConvergence){
                    convergenceTime++;
                }else{
                    convergenceTime = 0;
                }
                //
                if (convergenceTime == GlobalConfiguration.CONVERGENCE_CONFIDENCE_TIMES){
                    convergence = true;
                }
            }
        }while(!convergence);
        System.out.println(simulationResults.printResults().prettyPrint());
    }

    private static WMBusStats performSimulation(SimulationNetworkWithDistance simulationWrapper, boolean withHamming, boolean withWakeup)  {

        WMBusSimulation simulation = new WMBusSimulation(simulationWrapper.network,withHamming,withWakeup);
        simulation.run();
        return simulation.getResults();
    }
}
