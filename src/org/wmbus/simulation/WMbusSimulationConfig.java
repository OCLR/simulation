package org.wmbus.simulation;

import java.util.Random;

public class WMbusSimulationConfig {

    public static final double CONF_SIMULATION_CONVERGENCE_PERCENTAGE = 1;
    public static final double CONF_SIMULATION_STABILITY_TIMES = 100;

    public  final Random CONF_RANDOM = new Random();

    public boolean CONF_HAMMING = false;
    public boolean CONF_WAKEUP = false;



    public WMbusSimulationConfig(boolean enableHamming, boolean enableWakeUpAlgorithm){
        this.CONF_HAMMING = enableHamming;
        this.CONF_WAKEUP = enableWakeUpAlgorithm;
    }
}
