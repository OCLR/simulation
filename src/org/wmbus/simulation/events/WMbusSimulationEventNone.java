package org.wmbus.simulation.events;

import java.util.ArrayList;

public class WMbusSimulationEventNone implements WMbusSimulationEventInterface {
    public void provideMeasure(double measure, int convergenceState, double convergencePercentage){

    }

    @Override
    public void pathStart(boolean requestType) {

    }

    @Override
    public void pathEnd(boolean success) {

    }

    @Override
    public void pathRequest(int source, int destination, double distance) {

    }

    @Override
    public void pathPredict(Integer destination, ArrayList<Integer> path) {

    }

    @Override
    public void globalPathEnd(boolean b) {

    }

}
