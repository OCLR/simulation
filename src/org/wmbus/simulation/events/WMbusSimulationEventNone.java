package org.wmbus.simulation.events;

import org.wmbus.protocol.messages.Response;

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

    @Override
    public void masterResponseReceived(Response res) {

    }

}
