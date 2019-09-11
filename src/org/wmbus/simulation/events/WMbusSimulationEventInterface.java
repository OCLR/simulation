package org.wmbus.simulation.events;

public interface WMbusSimulationEventInterface {
    public void provideMeasure(double measure, int convergenceState, double convergencePercentage);

}
