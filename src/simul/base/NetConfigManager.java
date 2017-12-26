package simul.base;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Federico Falconi on 29/06/2017.
 */

public class NetConfigManager {
    private final int NOISE_VARIABILITY;
    private final int MEDIUM_DEGREE;
    private final double MEDIUM_NOISE;

    private NetConfig config;
    private ArrayList<AddressesPair> edgeList = new ArrayList<AddressesPair>();

    /**
     * Graph generation
     * @param nodesNum Nodes number
     * @param variability 
     * @param mediumNoise
     * @param mediumDegree
     */
    public NetConfigManager(int nodesNum, int variability, double mediumNoise, int mediumDegree) {
        if (nodesNum < 3) {
            System.out.println("Il graph size is three.");
            nodesNum = 3;
        }	
        /**
         * Node creation.
         * 
         */
        config = new NetConfig(nodesNum);
        NOISE_VARIABILITY = variability;
        MEDIUM_NOISE = mediumNoise;
        MEDIUM_DEGREE = mediumDegree;

        int edgeEnd;
        double edgeNoise;

        Random randomGen = new Random();
        edgeEnd = randomGen.nextInt(nodesNum - 2) + 1;
        edgeNoise = MEDIUM_NOISE - ((MEDIUM_NOISE/2) * randomGen.nextDouble()); // Starting noise.

        config.setEdge(0, edgeEnd, edgeNoise);
        edgeList.add(new AddressesPair(0, edgeEnd));

        for (int i = 1; i < nodesNum; i++) { // generate all nodes.
        	
            for (int edgeNum = randomGen.nextInt(MEDIUM_DEGREE) + 1; edgeNum > 0; edgeNum--) {
                edgeEnd = randomGen.nextInt(nodesNum - 1);
                // bug fix ?_?
                if (i == edgeEnd) {
                    if (edgeNum == 1) {
                        edgeEnd--;
                    }
                    else {
                        continue;
                    }
                }

                if (!edgeList.contains(new AddressesPair(edgeEnd, i)) && !edgeList.contains(new AddressesPair(i, edgeEnd))) {
                    edgeNoise = MEDIUM_NOISE - ((MEDIUM_NOISE/3) * randomGen.nextDouble()); // reduce the noise.
                    // add to the edge list the node.
                    edgeList.add(new AddressesPair(i, edgeEnd));
                    // add the edge to the graph noise matrix.
                    config.setEdge(i, edgeEnd, edgeNoise);
                }
            }
        }
        
    }


    public void updateNoise() {
        double newNoise;
        Random randomGen = new Random();

        for (AddressesPair edge: edgeList) {
            if (randomGen.nextInt(100) <= NOISE_VARIABILITY) {
                newNoise = MEDIUM_NOISE - ((MEDIUM_NOISE/2) * randomGen.nextGaussian());
                config.setEdge(edge.getFirst(), edge.getSecond(), newNoise);
                config.setEdge(edge.getSecond(), edge.getFirst(), newNoise);
            }
        }
    }


    public HashMap<Integer, Double> getOutgoingEdges(int source) {
        HashMap<Integer, Double> outgoingEdges = new HashMap<Integer, Double>();

        for (AddressesPair edge : edgeList) {
            if (edge.getFirst() == source) {
                outgoingEdges.put(edge.getSecond(), config.getEdge(source, edge.getSecond()));
            }
            else if (edge.getSecond() == source) {
                outgoingEdges.put(edge.getFirst(), config.getEdge(source, edge.getFirst()));
            }
        }

        return outgoingEdges;
    }


    public SimpleWeightedGraph<Integer, DefaultWeightedEdge> getGraphRepresentation() {
        SimpleWeightedGraph<Integer, DefaultWeightedEdge> graph =
                new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);

        int leftEdgeSide;
        int rightEdgeSide;

        for (AddressesPair edge : edgeList) {
            leftEdgeSide = edge.getFirst();
            rightEdgeSide = edge.getSecond();

            graph.addVertex(leftEdgeSide);
            graph.addVertex(rightEdgeSide);

            graph.setEdgeWeight(graph.addEdge(leftEdgeSide, rightEdgeSide),
                    config.getEdge(leftEdgeSide, rightEdgeSide));
        }

        return graph;
    }


    public void readMatrix() {
        for (int i = 0; i < config.NODES; i++) {
            for (int j = 0; j < config.NODES; j++) {
                System.out.print(config.getEdge(i, j) + " ");
            }
            System.out.println();
        }
    }
}
