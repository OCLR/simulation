package org.examples.packetSizeTest;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.wmbus.protocol.config.WMBusDeviceConfig;
import org.wmbus.protocol.infrastructure.ECCTable;
import org.wmbus.protocol.messages.Response;
import org.wmbus.simulation.WMBusSimulation;
import org.wmbus.simulation.WMbusSimulationConfig;
import org.wmbus.simulation.convergence.model.ConvergenceModel;
import org.wmbus.simulation.convergence.model.InitialSkipWithTimesAverageConvergenceModel;
import org.wmbus.simulation.convergence.model.config.InitialSkipTimesAverageConvergenceConfigModel;

import java.util.ArrayDeque;

public class ResponseSize  {
    public static void main(String[] args) {
        ArrayDeque<ECCTable> ecc = new ArrayDeque<ECCTable>();
        System.out.println("MessageSize;PayloadSize;Hamming=false");
        WMBusSimulation sim = ResponseSize.generateSimulation(false);
        Response r = new Response(sim,10,10,ecc);
        // System.out.println("Message Size (header+payload):"+r.getMessageSize()+" bit");
        System.out.println("0;"+r.getMessageSize()+";"+r.getMessageSizeOnlyPayloadWithParitybit());
        // System.out.println("Header part:" + r.getMessageHeader()+ " bit");
        //System.out.println("Payload part:" + r.getMessageSizeOnlyPayloadWithParitybit() + " bit");
        //System.out.println("Payload part with no parity:" + r.getMessageSizeOnlyPayloadWithoutParitybit() + " bit");
        //System.out.println("Message blocks:" + r.getMessageBlockCount()); // 4 for data and 1 for header.
        System.out.println("1 node");
        ECCTable t = new ECCTable(10);
        t.getEntries().put(10,10);
        r.getECCTables().add(t);
        System.out.println(r.getECCTables().getFirst().getEntries().size()+";"+r.getMessageSize()+";"+r.getMessageSizeOnlyPayloadWithParitybit());
        r.getECCTables().remove(t);
        t.getEntries().put(11,10);
        r.getECCTables().add(t);
        System.out.println(r.getECCTables().getFirst().getEntries().size()+";"+r.getMessageSize()+";"+r.getMessageSizeOnlyPayloadWithParitybit());
        r.getECCTables().remove(t);
        t.getEntries().put(12,30);
        r.getECCTables().add(t);
        System.out.println(r.getECCTables().getFirst().getEntries().size()+";"+r.getMessageSize()+";"+r.getMessageSizeOnlyPayloadWithParitybit());
        r.getECCTables().remove(t);
        t.getEntries().put(13,20);
        r.getECCTables().add(t);
        System.out.println(r.getECCTables().getFirst().getEntries().size()+";"+r.getMessageSize()+";"+r.getMessageSizeOnlyPayloadWithParitybit());
        r.getECCTables().remove(t);
        t.getEntries().put(14,10);
        r.getECCTables().add(t);
        System.out.println(r.getECCTables().getFirst().getEntries().size()+";"+r.getMessageSize()+";"+r.getMessageSizeOnlyPayloadWithParitybit());
        r.getECCTables().remove(t);
        t.getEntries().put(15,10);
        r.getECCTables().add(t);
        System.out.println(r.getECCTables().getFirst().getEntries().size()+";"+r.getMessageSize()+";"+r.getMessageSizeOnlyPayloadWithParitybit());
        r.getECCTables().remove(t);
        t.getEntries().put(16,10);
        r.getECCTables().add(t);
        System.out.println(r.getECCTables().getFirst().getEntries().size()+";"+r.getMessageSize()+";"+r.getMessageSizeOnlyPayloadWithParitybit());


         ecc = new ArrayDeque<ECCTable>();
        System.out.println("MessageSize;PayloadSize;Hamming=TRUE");
        sim = ResponseSize.generateSimulation(true);
        r = new Response(sim,10,10,ecc);
        // System.out.println("Message Size (header+payload):"+r.getMessageSize()+" bit");
        System.out.println("0;"+r.getMessageSize()+";"+r.getMessageSizeOnlyPayloadWithParitybit());
        // System.out.println("Header part:" + r.getMessageHeader()+ " bit");
        //System.out.println("Payload part:" + r.getMessageSizeOnlyPayloadWithParitybit() + " bit");
        //System.out.println("Payload part with no parity:" + r.getMessageSizeOnlyPayloadWithoutParitybit() + " bit");
        //System.out.println("Message blocks:" + r.getMessageBlockCount()); // 4 for data and 1 for header.
        System.out.println("1 node");
        t = new ECCTable(10);
        t.getEntries().put(10,10);
        r.getECCTables().add(t);
        System.out.println(r.getECCTables().getFirst().getEntries().size()+";"+r.getMessageSize()+";"+r.getMessageSizeOnlyPayloadWithParitybit());
        r.getECCTables().remove(t);
        t.getEntries().put(11,10);
        r.getECCTables().add(t);
        System.out.println(r.getECCTables().getFirst().getEntries().size()+";"+r.getMessageSize()+";"+r.getMessageSizeOnlyPayloadWithParitybit());
        r.getECCTables().remove(t);
        t.getEntries().put(12,30);
        r.getECCTables().add(t);
        System.out.println(r.getECCTables().getFirst().getEntries().size()+";"+r.getMessageSize()+";"+r.getMessageSizeOnlyPayloadWithParitybit());
        r.getECCTables().remove(t);
        t.getEntries().put(13,20);
        r.getECCTables().add(t);
        System.out.println(r.getECCTables().getFirst().getEntries().size()+";"+r.getMessageSize()+";"+r.getMessageSizeOnlyPayloadWithParitybit());
        r.getECCTables().remove(t);
        t.getEntries().put(14,10);
        r.getECCTables().add(t);
        System.out.println(r.getECCTables().getFirst().getEntries().size()+";"+r.getMessageSize()+";"+r.getMessageSizeOnlyPayloadWithParitybit());
        r.getECCTables().remove(t);
        t.getEntries().put(15,10);
        r.getECCTables().add(t);
        System.out.println(r.getECCTables().getFirst().getEntries().size()+";"+r.getMessageSize()+";"+r.getMessageSizeOnlyPayloadWithParitybit());
        r.getECCTables().remove(t);
        t.getEntries().put(16,10);
        r.getECCTables().add(t);
        System.out.println(r.getECCTables().getFirst().getEntries().size()+";"+r.getMessageSize()+";"+r.getMessageSizeOnlyPayloadWithParitybit());
    }

    public static WMBusSimulation  generateSimulation( boolean hamming){

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
        WMBusDeviceConfig wmbusDeviceConfig = new WMBusDeviceConfig(-10,
                2,
                3,
                -70);
        // -70dbm
        double success = 0;
        double failure = 0;
        double count = 0;

        SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> network = new SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        network.addVertex(0);
        network.addVertex(1);
        DefaultWeightedEdge edge = network.addEdge(0, 1);
        network.setEdgeWeight(edge, 10);

        return new WMBusSimulation(network, wmbusDeviceConfig, config, simulationConvergence, null);
    }
}
