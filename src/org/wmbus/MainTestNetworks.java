package org.wmbus;

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
        ResultTable res = MainTestNetworks.convergesResult(Integer.parseInt(args[0]),Boolean.parseBoolean(args[1]),Boolean.parseBoolean(args[2]));
        System.out.println(res.printHeader(true));
        System.out.println(res.printValues(true));
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
        long result = 0, preresult = 0;
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
                simulationResults = MainTestNetworks.performSimulation(attempt, withHamming, withWakeup);
                preresult = result;
                result = simulationResults.masterTrasmissionFaultWithUpdate + simulationResults.masterTrasmissionFaultWithNoUpdate;
                convergencePerc = ((preresult)*GlobalConfiguration.CONVERGENCE_CONFIDENCE_PERCENTAGE)/100;
                /*
                    Convergence algorithm.
                 */
                boolean tempConvergence = ( (result-preresult) <= convergencePerc);
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
        return simulationResults.printResults();
    }

    private static WMBusStats performSimulation(SimulationNetworkWithDistance simulationWrapper, boolean withHamming, boolean withWakeup)  {

        WMBusSimulation simulation = new WMBusSimulation(simulationWrapper.network,withHamming,withWakeup);
        simulation.run();
        return simulation.getResults();
    }
}
