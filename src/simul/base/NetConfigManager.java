package simul.base;

import java.security.SecureRandom;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by Federico Falconi on 29/06/2017.
 */

public class NetConfigManager {
    private final int NOISE_VARIABILITY;
    private final int MEDIUM_DEGREE;
    private final int NOISE_POWER_PERC;
    
    private static int PERCENT_ZERO = 33;
    private static int PERCENT_ONE = 33;
    private static int PERCENT_TWO = 33;
    

    private NetConfig config;
    private ArrayList<AddressesPair> edgeList = new ArrayList<AddressesPair>();
    private final static Random randomGen = new SecureRandom();
    private int NOISE_RANGE;
    private int MAX_DEST;
    private final int NODES_NOISE_PERC;

    /**
     * Graph generation
     * @param nodesNum Nodes number
     * @param variability 
     * @param mediumNoise
     * @param mediumDegree
     * @param noiseRange 
     * @param packetDestinationMax 
     */
    public NetConfigManager(int nodesNum, int variability, int noisePowerPerc, int mediumDegree, int noiseNodesPercentage, int packetDestinationMax) {
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
        NOISE_POWER_PERC = noisePowerPerc;
        MEDIUM_DEGREE = mediumDegree;
        NODES_NOISE_PERC = noiseNodesPercentage;
        setMaxDest(packetDestinationMax);
        int edgeEnd;
        double edgeNoise;

        
        edgeEnd = randomGen.nextInt(nodesNum - 2) + 1;
        edgeNoise = NOISE_POWER_PERC * randomGen.nextGaussian(); // Starting noise.
        
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
                    edgeNoise = NOISE_POWER_PERC - ((NOISE_POWER_PERC/3) * randomGen.nextDouble()); // reduce the noise.
                    // add to the edge list the node.
                    edgeList.add(new AddressesPair(i, edgeEnd));
                    // add the edge to the graph noise matrix.
                    config.setEdge(i, edgeEnd, updateSingleNoise());
                }
            }
        }
        
    }
    public static double getValidNoise(double noise){
        return noise>2?0:noise;
    }
    
    public static double updateSingleNoise(){
        
        int var = randomGen.nextInt(100); 
        if (var < NetConfigManager.PERCENT_ZERO){
          return 0;
        } else if (var <= NetConfigManager.PERCENT_ONE) {
           return 1;
        }   
        return 2;

    }

    public void updateNoise(long messageCount) {
        double newNoise;
        int infected = 0; // edge's number with noise changed
        int nodesNumber = (NODES_NOISE_PERC*edgeList.size())/100;
        LinkedList<Integer> nodes = new LinkedList<Integer>();
        
        if (messageCount % NOISE_VARIABILITY != 0){
            return; // Every n packet.. apply rumor.
        }
        
        do{
            nodes.add(randomGen.nextInt(this.config.NODES - 1) + 1);
        }while(nodes.size()!=nodesNumber);
        // for every nodes ... let's spread the noise.
        for( Integer node: nodes){
            
            infected = (MEDIUM_DEGREE*NOISE_RANGE)/100;
            for (AddressesPair edge: edgeList) {
               if (edge.getFirst() == node && infected > 0 ){
                   infected--;
                   newNoise = updateSingleNoise();
                   config.setEdge(edge.getFirst(), edge.getSecond(), newNoise);
                   config.setEdge(edge.getSecond(), edge.getFirst(), newNoise);
               }
            }
        }
        /*if (startNode + nodesNumber < edgeList.size()){
            endNode = startNode + nodesNumber;
        }else{
            startNode = randomGen.nextInt(edgeList.size() - nodesNumber);
            endNode = startNode +nodesNumber;
        }*/
        
        
        //List<AddressesPair> edges = edgeList.subList(startNode, endNode);
        /*
        for (AddressesPair edge: edges) {
        	//&& infected < NOISE_RANGE
            if (randomGen.nextInt(100) + 1 <= NOISE_VARIABILITY ) {
                //newNoise = MEDIUM_NOISE - ((MEDIUM_NOISE/2) * randomGen.nextDouble()); // nextGuassian
                newNoise = this.updateSingleNoise();
                //newNoise = config.getEdge(edge.getFirst(), edge.getSecond());
                // -MEDIUM_NOISE ... +MEDIUM_NOISE
                //newNoise = newNoise + ((randomGen.nextDouble() % (MEDIUM_NOISE*2+1)) - MEDIUM_NOISE);
                // setting limit 
                //if (newNoise < 0){ 
                //    newNoise = 0;
                //} else if (newNoise >1){
                //    newNoise = 1;
                //}
                // Three state. Intensity 
                //newNoise = randomGen.nextInt(NOISE_RANGE) != 0? randomGen.nextInt(2) :Integer.MAX_VALUE;
                
                config.setEdge(edge.getFirst(), edge.getSecond(), newNoise);
                config.setEdge(edge.getSecond(), edge.getFirst(), newNoise);
                //infected++;
            }
        }*/
    	/*double newNoise;
    	// Localize to a specific node.
    	int infected = 0; // edge's number with noise changed.
    	
    	int node = randomGen.nextInt(this.config.NODES - 1) +1;
    	if (randomGen.nextInt(100) <= NOISE_VARIABILITY) {
    		for (AddressesPair edge: edgeList) {
    			if (edge.getFirst() == node && infected < NOISE_RANGE){
    				infected++;
    				newNoise = MEDIUM_NOISE - ((MEDIUM_NOISE/2) * randomGen.nextGaussian());
    				config.setEdge(edge.getFirst(), edge.getSecond(), newNoise);
                    config.setEdge(edge.getSecond(), edge.getFirst(), newNoise);
    			}
        	}
    	}else {
    		for (AddressesPair edge: edgeList) {
    			if (edge.getFirst() != node && edge.getSecond() != node){
    				newNoise = 0;
    				config.setEdge(edge.getFirst(), edge.getSecond(), newNoise);
                    config.setEdge(edge.getSecond(), edge.getFirst(), newNoise);
    			}
        	}
    	}*/
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
    
    public ArrayList<Integer> getNeighbors(int source) {
    	ArrayList<Integer> outgoingEdges = new ArrayList<Integer>();

        for (AddressesPair edge : edgeList) {
        	// edges are not repeated.
            if (edge.getFirst() == source) {
                outgoingEdges.add(edge.getSecond());
            }
            if (edge.getSecond() == source) {
                outgoingEdges.add(edge.getFirst());
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


	public int getMaxDest() {
		return MAX_DEST;
	}


	public void setMaxDest(int mAX_DEST) {
		MAX_DEST = mAX_DEST;
	}
}
