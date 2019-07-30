package org.wmbus.simulation.stats;

public class WMBusSingleCommunicationStat {
    private int source;
    private int destination;
    private double distance;
    private double ber;

    public WMBusSingleCommunicationStat(int source, int destination, double distance, double ber) {
        this.source = source;
        this.destination = destination;
        this.distance = distance;
        this.ber = ber;
    }

    public int getSource() {
        return source;
    }

    public int getDestination() {
        return destination;
    }

    public double getDistance() {
        return distance;
    }

    public double getBer() {
        return ber;
    }
}
