package simul.simul;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import simul.infrastructure.WMbusNetwork;
import simul.nodes.MasterGraphNode;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class CompleteTestProtocol {
    private WMbusNetwork network;


    public CompleteTestProtocol(SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> distanceGraph, int lasting) throws IOException {
        this.network = new WMbusNetwork(distanceGraph, lasting);
    }

    public String description() {
        /*return "Questo modello descrive il protocollo per utility network " +
                "proposto dal professor Culmone. Quindi il routing si basa su " +
                "una sequenza di hop codificata nell'header del pacchetto.";*/
        return " This model describes a protocol for utility network, proposed by prof. Culmone." +
                " Therefore the routing is based on a precomputed sequence encoded with the packet header from master";
    }


    public void run() throws Exception {
        this.network.run();
    }

    public static void main(String[] args) throws Exception {

        SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> network = null;
        CompleteTestProtocol protocol = new CompleteTestProtocol(network,50000);
        protocol.run();

    }
