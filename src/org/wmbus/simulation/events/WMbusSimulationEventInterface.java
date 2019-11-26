package org.wmbus.simulation.events;

import org.wmbus.protocol.messages.Response;

import java.util.ArrayList;

public interface WMbusSimulationEventInterface {
    public void provideMeasure(double measure, int convergenceState, double convergencePercentage);
    public void pathStart(boolean requestType);
    public void pathEnd(boolean success);
    public void pathRequest(int source, int destination, double distance);

    public void pathPredict(Integer destination, ArrayList<Integer> path);

    public void globalPathEnd(boolean b);

    public void masterResponseReceived(Response res);
}
