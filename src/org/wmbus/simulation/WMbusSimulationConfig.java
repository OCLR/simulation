package org.wmbus.simulation;

import java.util.Random;

public class WMbusSimulationConfig {
    public  final Random CONF_RANDOM = new Random();
    public boolean CONF_HAMMING = false;
    public boolean CONF_WAKEUP = false;
    public boolean CONF_DETAILED_NOISE = false;
    public int CONF_DETAILED_NOISE_SUBLEVEL = 256;
    public boolean CONF_SLAVE_RESULT = true;


    public WMbusSimulationConfig(boolean enableHamming, boolean enableWakeUpAlgorithm, boolean enableDetailedNoiseCommunication){
        this.CONF_HAMMING = enableHamming;
        this.CONF_WAKEUP = enableWakeUpAlgorithm;
        this.CONF_DETAILED_NOISE = enableDetailedNoiseCommunication;
    }
}
