package org.examples;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.wmbus.protocol.config.WMBusDeviceConfig;
import org.wmbus.protocol.messages.CustomSizeMessage;
import org.wmbus.protocol.messages.Response;
import org.wmbus.protocol.messages.WMBusCommunicationState;
import org.wmbus.protocol.nodes.WMbusDevice;
import org.wmbus.simulation.WMBusSimulation;
import org.wmbus.simulation.WMbusSimulationConfig;
import org.wmbus.simulation.convergence.model.ConvergenceModel;
import org.wmbus.simulation.convergence.model.InitialSkipWithTimesAverageConvergenceModel;
import org.wmbus.simulation.convergence.model.config.InitialSkipTimesAverageConvergenceConfigModel;
import org.wmbus.simulation.events.WMbusSimulationEventInterface;

import java.util.ArrayList;

import static org.wmbus.simulation.convergence.state.ConvergenceState.CONVERGENCE_STATE_STOP_CONVERGENCE;

final class MyResult {
    private final double first;
    private final double second;

    public MyResult(double first, double second) {
        this.first = first;
        this.second = second;
    }

    public double getFirst() {
        return first;
    }

    public double getSecond() {
        return second;
    }
}

public class SimulationDistanceToPer implements WMbusSimulationEventInterface {
    public static void main(String[] args) throws Exception {
        SimulationDistanceToPer s = new SimulationDistanceToPer();
        ArrayList<Integer> packetSizeReq = new ArrayList<Integer>();
        ArrayList<Integer> packetSizeRes = new ArrayList<Integer>();
        int packetSize;
        boolean hamming;
        MyResult result;
        double ber;
        float powerDbm = 10;
        float noiseDbm = -75;

        packetSizeRes.add(40);
        packetSizeRes.add(80);
        packetSizeRes.add(120);

        packetSizeReq.add(20);
        packetSizeReq.add(40);
        packetSizeReq.add(60);

        for (int hammingIndex = 0; hammingIndex < 2; hammingIndex++) {
            hamming = (hammingIndex == 1);

            for (int i = 0; i < packetSizeReq.size(); i++) {
                packetSize = packetSizeReq.get(i);

                System.out.println("PowerDbm="+powerDbm+",NoiseDbm="+noiseDbm+",WithHamming="+(hamming?"Yes":"No")+",PacketType=Request,PayloadSizeNoParityBit="+packetSize);
                for (int distance = 5; distance < 251; distance++) {

                    result = s.performSimulation(hamming,powerDbm,noiseDbm,packetSize,distance);
                    System.out.println(distance+","+result.getFirst()+","+result.getSecond());
                }
            }

            // System.out.println((hamming)?"Hamming":"NotHamming"+",Response");
            for (int i = 0; i < packetSizeRes.size(); i++) {
                packetSize = packetSizeRes.get(i);
                System.out.println("PowerDbm="+powerDbm+",NoiseDbm="+noiseDbm+",WithHamming="+(hamming?"Yes":"No")+",PacketType=Request,PayloadSizeNoParityBit="+packetSize);
                for (int distance = 5; distance < 251; distance++) {
                     result = s.performSimulation(hamming,powerDbm, noiseDbm, packetSize,distance);
                     System.out.println(distance+","+result.getFirst()+","+result.getSecond());
                }
            }

        }



    }

    public MyResult performSimulation(boolean hamming, float powerDbm, float noiseDbm, int packetSize, double distance) throws Exception {
        WMbusSimulationConfig config =  new WMbusSimulationConfig(
                hamming,
                false,
                false
        );
        ConvergenceModel simulationConvergence = new InitialSkipWithTimesAverageConvergenceModel(new InitialSkipTimesAverageConvergenceConfigModel(
                10000,
                1,
                5,
                10,
                10
        ));
        WMBusDeviceConfig wmbusDeviceConfig = new WMBusDeviceConfig(powerDbm,
                2,
                3,
                noiseDbm);
        // -70dbm
        double success = 0;
        double failure = 0;
        double count = 0;

        SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> network = new SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        network.addVertex(0);
        network.addVertex(1);
        DefaultWeightedEdge edge = network.addEdge(0, 1);
        network.setEdgeWeight(edge, distance);

        WMBusSimulation simulation = new WMBusSimulation(network, wmbusDeviceConfig, config, simulationConvergence, this);
        // Just get the probability of error for the packet.
        do {
            WMbusDevice destinationMbusNode = simulation.getwMbusNetwork().getNode(1);
            CustomSizeMessage message = new CustomSizeMessage(simulation, 0, 1, packetSize);
            double nodeRes = destinationMbusNode.receiveAck(message); // son.getHopDestination()
            count++;
            if (WMBusCommunicationState.isOK(nodeRes)) {
                success++;
            } else {
                failure++;
            }
        } while( simulationConvergence.addMeasure((failure/count)*100)!= CONVERGENCE_STATE_STOP_CONVERGENCE);
        double ber = simulation.getwMbusNetwork().getBer(0,1);

        return new MyResult(failure/count*100,ber);
    }

    @Override
    public void provideMeasure(double measure, int convergenceState, double convergencePercentage) {

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
