package org.wmbus.protocol.utilities;

import org.wmbus.simulation.WMBusSimulation;

public class Hamming {

    public static double getSuccessProb(WMBusSimulation simulation, long n, double ber){
        double neg_ber = (1-ber);

        double noerror= Math.pow(1-ber,n); // (1-r)^n
        double oneerror= Math.pow(neg_ber,n-1)*n*ber; // (1-r)^(n-1)*n*ber
        double morethanoneerror = 1-(noerror+oneerror);
        double randomValue = simulation.getwMbusSimulationConfig().CONF_RANDOM.nextDouble();
        return noerror;
    }
    public static double getRecoverableProb(WMBusSimulation simulation,long n,double ber){
        double neg_ber = (1-ber);

        double noerror= Math.pow(1-ber,n); // (1-r)^n
        double oneerror= Math.pow(neg_ber,n-1)*n*ber; // (1-r)^(n-1)*n*ber
        double morethanoneerror = 1-(noerror+oneerror);
        double randomValue = simulation.getwMbusSimulationConfig().CONF_RANDOM.nextDouble();
        return oneerror;
    }
    public static double getFaultProb(WMBusSimulation simulation,long n,double ber){
        double neg_ber = (1-ber);
        double noerror= Math.pow(1-ber,n); // (1-r)^n
        double oneerror= Math.pow(neg_ber,n-1)*n*ber; // (1-r)^(n-1)*n*ber
        double morethanoneerror = 1-(noerror+oneerror);
        double randomValue = simulation.getwMbusSimulationConfig().CONF_RANDOM.nextDouble();
        if (simulation.getwMbusSimulationConfig().CONF_HAMMING){
            return  morethanoneerror;
        }else{
            return  oneerror+morethanoneerror;
        }

    }
}
