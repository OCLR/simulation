package simul.protocol;


import desmoj.core.simulator.*;
import co.paralleluniverse.fibers.SuspendExecution;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import simul.infrastructure.MbusDevice;
import simul.infrastructure.MbusMessage;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Random;

/**
 * Created by Federico Falconi on 04/07/2017.
 */
public class Master extends MbusDevice {
    private int token = 0;
    private int sended = 0;
    private int returned = 0;

    private List<MasterGraphNode> path;
    private SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> graph;

    private Random randomGen = new Random();


    public Master(Model owner, Boolean showInTrace, SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> graph) {
        super(owner, "Master", showInTrace, 0);
        this.graph = graph;
    }


    private boolean decode(MbusMessage message, DefaultWeightedEdge edge) throws SuspendExecution{
        double sum = 0;
        boolean integrity = true;

        if ((message.getErrors(1) < 3) && (message.getErrors(2) < 3)) {
            sum += (message.getErrors(1) + message.getErrors(2));
        }
        else {
            return false;
        }

        for (int i = 0; i < message.getLength(); i++) {
            if (message.getErrors(i) > 1) {
                integrity = false;
                sum += 2;
            }
            else if (message.getErrors(i) == 1) {
                sum += 1;
            }
        }

        graph.setEdgeWeight(edge, (graph.getEdgeWeight(edge) * 2 + (sum / message.getLength())) / 3);

        if (!integrity  && (message.getClass() == Response.class)) { // package fault 
            if(((Response)message).getNextHop() == 0) {
                System.out.println("Packet fault");
            }
        }

        return integrity;
    }


    private MasterGraphNode findNode(int nodeAddress) {
        for (MasterGraphNode vertex : graph.vertexSet()) {
            if (vertex.getStaticAddress() == nodeAddress)
                return vertex;
        }

        return null;
    }


    private ArrayDeque<Integer> encodePath(List<MasterGraphNode> path) {
        ArrayDeque<Integer> hopList = new ArrayDeque<Integer>();

        for (MasterGraphNode node : path) {
            hopList.add(node.getStaticAddress());
        }

        return hopList;
    }


    private void readNoiseTables(ArrayDeque<NoiseTable> tables) {
        DefaultWeightedEdge currentEdge;
        NoiseTable currentTable;

        try {
            for (MasterGraphNode node : path) {

                currentTable = tables.pop();

                for (Integer address : currentTable.getEntries().keySet()) {
                    currentEdge = graph.getEdge(node, findNode(address));

                    graph.setEdgeWeight(currentEdge, currentTable.getEntries().get(address));
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("The noise table rumor size (" + tables.size() +
                    ") and the path's length (" + path.size() + ") doesn't match.");
        }
    }


    public void lifeCycle() throws SuspendExecution {
        boolean correct;
        Request sending;
        TimeInstant timer;
        MasterGraphNode endNode;
        MasterGraphNode masterNode = findNode(0);
        ShortestPathAlgorithm.SingleSourcePaths<MasterGraphNode, DefaultWeightedEdge> dijkstraPaths;

        System.out.println(graph);

        while (true) {
            dijkstraPaths = new DijkstraShortestPath<MasterGraphNode, DefaultWeightedEdge>(graph).getPaths(masterNode);
            endNode = findNode(randomGen.nextInt(graph.vertexSet().size() - 1) + 1);

            /**
             * The dijkstraPaths path.
             */
            path = dijkstraPaths.getPath(endNode).getVertexList();
            path.remove(0);
            

            /**
             * Prepare packet
             * 1 represents the type of packet
             * token represents the sequence number.
             * 20 payload size.
             * 0 the source 
             * path the list of nodes.
             */
            sending = new Request(1, token++, 20, 0, encodePath(path));

            correct = false;
            /**
             * Send packet and update rumors node.
             */
            transmit(sending, true);

            System.out.println();
            System.out.println();
            System.out.println("Sended " + ++sended + " Received: " + returned + ", Time: " + presentTime());
            System.out.println("Sent packet with header size: " + (sending.getHopList().size() + 4));
            
            // wait for a while.
            timer = TimeOperations.add(presentTime(), new TimeSpan(path.size() * 5 * sending.getLength()));
            activate(timer);

            while (true) {
                passivate();
                // wait until i receive something.
                if (getReceived() == null) { // TIMEOUT 
                	// not correct? PACKET LOSS.
                    if (!correct) { // double check not required.
                        System.out.println("Packet loss");
                        // Update the weight of graph.
                        for (DefaultWeightedEdge edge : dijkstraPaths.getPath(endNode).getEdgeList()) {
                            graph.setEdgeWeight(edge, (graph.getEdgeWeight(edge) * 2 + 2) / 3);
                        }
                    }

                    break;
                }
                else {
                    if (getReceived().getClass() == Response.class) { // Receive a message..
                        Response received = (Response)retrieveMsg(); // read packet.

                        if (decode(received, graph.getEdge(masterNode, findNode(received.getSource())) )) { // decode the message 
                            if (received.getNextHop() == 0) {
                                correct = true;
                                System.out.println("Packet received");
                                readNoiseTables(received.getNoiseTables());
                                returned++;
                            }
                        }
                    }
                    else {
                        Request received = (Request)retrieveMsg(); // read as a request and decode.  (doing nothing)
                        decode(received, graph.getEdge(masterNode, findNode(received.getSource())));
                    }
                }
                
            }
        }
    }
}
