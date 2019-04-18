package simul.base;

import static java.lang.Math.abs;
import java.security.SecureRandom;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import simul.protocol.Request;
import simul.protocol.Response;
import simul.protocol.Stats;

/**
 * Created by Federico Falconi on 29/06/2017.
 */
public class NetConfigManager {

    private final int NOISE_VARIABILITY;
    private int MEDIUM_DEGREE;
    private int MAX_DEGREE;
    
    private final int NOISE_POWER_PERC;

    private static int PERCENT_ZERO = 10;
    private static int PERCENT_ONE = 10;
    private static int PERCENT_TWO = 33;

    private NetConfig config;
    private ArrayList<AddressesPair> edgeList = new ArrayList<AddressesPair>();
    //private HashMap<Integer,Double> prob = new HashMap<Integer,Double>(); 
    private final static Random randomGen = new Random(); //SecureRandom
    //private int NOISE_RANGE;
    private int MAX_DEST;
    private final int NODES_NOISE_PERC;
    private final ArrayList<Integer> noiseVariability;

    /**
     * Graph generation
     * @param nodesNum
     * @param variability
     * @param noisePowerPerc
     * @param mediumDegree
     * @param noiseEdgesNodePercentage
     * @param packetDestinationMax
     */
    public NetConfigManager(int nodesNum,
                            int variability,  /* Variability for nodes  */
                            int noisePowerPerc, /* Noise nodes impact*/
                            int mediumDegree, /* */
                            int noiseEdgesNodePercentage,
                            int packetDestinationMax) {
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
        this.noiseVariability = equalDivisionSuccession(NOISE_VARIABILITY);
        
        
        
        NOISE_POWER_PERC = noisePowerPerc;
        MEDIUM_DEGREE = mediumDegree;
        NODES_NOISE_PERC = noiseEdgesNodePercentage;
        
        setMaxDest(packetDestinationMax);
        int edgeEnd;
        double edgeNoise;
        ArrayList<Integer> uniqueRandom;
        int s = MEDIUM_DEGREE;
        this.generateEdges(0,nodesNum);
        MAX_DEGREE = MEDIUM_DEGREE; // consequence 
        // this is beacuse in the average.
        MEDIUM_DEGREE = s;
        /*edgeEnd = randomGen.nextInt(nodesNum - 1) + 1;
        edgeNoise = NOISE_POWER_PERC * randomGen.nextGaussian(); // Starting noise.
        
        config.setEdge(0, edgeEnd, edgeNoise);
        edgeList.add(new AddressesPair(0, edgeEnd));*/
//        for (int i = 0; i < nodesNum; i++) { // generate all nodes.
//            uniqueRandom = new ArrayList<Integer>();
//            for (int edgeNum = MEDIUM_DEGREE + 1; edgeNum > 0; edgeNum--) {
//                // Create all edges.
//
//                edgeEnd = newNode(i, nodesNum, uniqueRandom);
//                uniqueRandom.add(edgeEnd);
//
//                if (edgeList.contains(new AddressesPair(edgeEnd, i)) || edgeList.contains(new AddressesPair(i, edgeEnd))) {
//                    continue; // exists go on.
//                }
//                if (i == edgeEnd) {
//                    System.out.println("what?");
//                }
//                // Choose a node that is different from the 
//                //if (!edgeList.contains(new AddressesPair(edgeEnd, i)) && !edgeList.contains(new AddressesPair(i, edgeEnd))) {
//                edgeNoise = 0;//NOISE_POWER_PERC - ((NOISE_POWER_PERC/3) * randomGen.nextDouble()); // reduce the noise.
//                // add to the edge list the node.
//                edgeList.add(new AddressesPair(i, edgeEnd));
//                // add the edge to the graph noise matrix.
//                config.setEdge(i, edgeEnd, edgeNoise);
//                //}
//            }
//            
//        }

    }
    public ArrayList<Integer> getNeighborns(int node){
        ArrayList<Integer> result = new ArrayList<Integer> ();
        for (int i = 0; i < edgeList.size();i++){
            if (edgeList.get(i).getFirst()==node){
                result.add(edgeList.get(i).getSecond());
            }
            if (edgeList.get(i).getSecond()==node){
                result.add(edgeList.get(i).getFirst());
            }
        }
        return result;
    }
    
    public int generateNode(int start,int nodesNum) {
        for (int s = start;s<nodesNum;s++){
            if (this.getNeighborns(s).size()<MEDIUM_DEGREE){
                return s;
            }
        }
        return nodesNum -1;
    }
    public void generateEdges(int startNode, int nodesNum){
        ArrayList<Integer> uniqueRandom;
        int edgeEnd,ns;
        double edgeNoise;
        boolean check;
        int stalemate = 0;
        ArrayList<Integer> nodes = new ArrayList<Integer>();
        check = false;
        ns = 0;
        uniqueRandom = this.getNeighborns(ns);
        
        // fill all the nodes.
        for (int i = 0; i < nodesNum;i++){
            nodes.add(i);
        }
        do { // generate all nodes.
            if (
                stalemate > nodesNum
                    ){ // increase 
                MEDIUM_DEGREE++;
            }
            if (uniqueRandom.size()>= MEDIUM_DEGREE){
                stalemate++;
            }else{
                stalemate = 0;
            }
            
            for (int edgeNum = uniqueRandom.size(); edgeNum < MEDIUM_DEGREE; edgeNum++) {
                // Create all edges.
                if (nodes.size() < MEDIUM_DEGREE){
                    for (int i = 0;i<nodes.size();i++){
                        if (!uniqueRandom.contains(nodes.get(i))&&i!=startNode){
                            check = true;
                            break;
                        }
                    }
                    
                }
                
                edgeEnd = newNode(ns, nodesNum, uniqueRandom, nodes);
                
                uniqueRandom.add(edgeEnd);
                
                if (ns == edgeEnd) {
                    System.out.println("what?");
                }else{
                    System.out.println("S:"+ns+ ",E:"+edgeEnd+" :"+uniqueRandom.toString());
                }
                // Choose a node that is different from the 
                //if (!edgeList.contains(new AddressesPair(edgeEnd, i)) && !edgeList.contains(new AddressesPair(i, edgeEnd))) {
                edgeNoise = 0;//NOISE_POWER_PERC - ((NOISE_POWER_PERC/3) * randomGen.nextDouble()); // reduce the noise.
                // add to the edge list the node.
                edgeList.add(new AddressesPair(ns, edgeEnd));
                // add the edge to the graph noise matrix.
                config.setEdge(ns, edgeEnd, edgeNoise);              
            }
            if (!check){
                nodes.remove(new Integer(ns));
                ns = uniqueRandom.get(randomGen.nextInt(uniqueRandom.size()-1));
                uniqueRandom = this.getNeighborns(ns);
                
            }
        } while (!nodes.isEmpty()&&!check);
    }

//    public static double getValidNoise(double noise) {
//        return noise > 2 ? 0 : noise;
//    }

    /**
     * < 1/2 http://www.maths.manchester.ac.uk/~pas/code/notes/part2.pdf
     *
     */
    public static double updateSingleNoise() {

//        int var = randomGen.nextInt(100); 
//        if (var < NetConfigManager.PERCENT_ZERO){
//          return 0;
//        } else if (var <= NetConfigManager.PERCENT_ONE) {
//           return 1;
//        }   
//        return 2;
        /*
        * The opposite beacuse the probability concerns the change.
        * We consider the model of a simmetric channel.
        */
        /**
         *randomGen.nextGaussian()) returns a range between [-1,+1] 
         * Using 0.25 as a mean and 0.75 as a standard deviation allows to restrict 
         * the domain in [0,1/2] 
         */
        double noise = ((randomGen.nextGaussian()) + 0.25) * 0.04;
        if (noise < 0){
            return 0;
        }else{
            if (noise > 0.5){
                return 0.5;
            }
        }
        return noise; // this noise should be between [0,0,5]
    }
    public double updateSingleRequest(Request req) {

//        int var = randomGen.nextInt(100); 
//        if (var < NetConfigManager.PERCENT_ZERO){
//          return 0;
//        } else if (var <= NetConfigManager.PERCENT_ONE) {
//           return 1;
//        }   
//        return 2;
        /*
        * The opposite beacuse the probability concerns the change.
        * We consider the model of a simmetric channel.
        */
        int size = 11;
        double r = config.getEdge(req.getSource(), req.getHopDestination());// channel error.
        double base = (1-r);
        double poneerror = base;
        double pnoerror, error;
        // p 1 error.
        for (int i = 0; i < size -1;i++){
           poneerror*=base;
        }
        // (1-r)^(n-1)
        pnoerror = poneerror;
        // (1-r)^n
        pnoerror*=base;
        // Multiply it for r.  (1-r)^(n-1)*r
        poneerror*=r;
        int canBe = 0;
        int numb = (req.getSize()/11);
        /*System.out.println("R:"+r);
        System.out.println("Size:"+size);
        System.out.println("Zero prob:"+pnoerror*100);
        System.out.println("1 prob:"+(poneerror + poneerror)*100);*/
        
        for (int i = 0; i < numb;i++){
            error = randomGen.nextDouble();
            if (error < pnoerror){
            } else if (error < poneerror + pnoerror){
                canBe++; // one error fine but not really
            }else{
                return 2; // otherwise cannot recover error.
            }
        }
        
       
        
        return (canBe/((req.getSize()/11)+0.0)); // No error means fine.
        
        //return 0.5-(randomGen.nextGaussian()*0.5);

    }

    public boolean updateNoise(long messageCount) {
        double newNoise;
        int infected = 0; // edge's number with noise changed
        int nodesNumber = (NOISE_POWER_PERC  * edgeList.size()) / 100;
        LinkedList<Integer> nodes = new LinkedList<Integer>();

        if (!this.noiseVariability.contains(new Integer((int) (messageCount % 101))) ){ // frequency
            return false;
        }
        
        //System.out.println("NOISE UPDATED Master:"+Stats.updateMasterSlave+" Slave:"+Stats.updateNoiseSlave);
        Stats.updateNoiseSlave = 0;
        Stats.updateMasterSlave = 0;
        Stats.windowFault = 0;
        do {
            nodes.add(randomGen.nextInt(this.config.NODES - 1) + 1);
        } while (nodes.size() != nodesNumber);
        // for every nodes ... let's spread the noise.
        for (Integer node : nodes) {

            infected = (MEDIUM_DEGREE * NODES_NOISE_PERC) / 100;
            for (AddressesPair edge : edgeList) {
                if (edge.getFirst() == node && infected > 0) {
                    infected--;
                    newNoise = updateSingleNoise();
                    config.setEdge(edge.getFirst(), edge.getSecond(), newNoise);
                    config.setEdge(edge.getSecond(), edge.getFirst(), newNoise);
                }
            }
        }
        return true;
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
            } else if (edge.getSecond() == source) {
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
        SimpleWeightedGraph<Integer, DefaultWeightedEdge> graph
                = new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);

        int leftEdgeSide;
        int rightEdgeSide;

        for (AddressesPair edge : edgeList) {
            leftEdgeSide = edge.getFirst();
            rightEdgeSide = edge.getSecond();

            graph.addVertex(leftEdgeSide);
            graph.addVertex(rightEdgeSide);
            System.out.println(leftEdgeSide + "->" + rightEdgeSide);
            
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

    private int newNode(int i, int nodesNum, ArrayList<Integer> list, ArrayList<Integer> nodes) {
        int node;
        boolean found ;
        int nInd;
        do {
            nInd = randomGen.nextInt(nodes.size() );
            node = nodes.get(nInd);
            found = false;
            
            
            for (int x = 0;x < list.size();x++){
                if (list.get(x)== node){
                    found = true;
                    break;
                }
            }
        } while (node == i || found);
        
        
            
        if (node == i) {
            System.out.println("what?");
        }else{
            //System.out.println(list.toString());
        }
        
        return node;
    }

    public double updateSingleResponse(Response son) {
         /*
        * The opposite beacuse the probability concerns the change.
        * We consider the model of a simmetric channel.
        */
        int size = 11;
        double r = config.getEdge(son.getSource(), son.getNextHop());// channel error.
        double base = (1-r);
        double poneerror = base;
        double pnoerror, error;
        // p 1 error.
        for (int i = 0; i < size -1;i++){
           poneerror*=base;
        }
        // (1-r)^(n-1)
        pnoerror = poneerror;
        // (1-r)^n
        pnoerror*=base;
        // Multiply it for r.  (1-r)^(n-1)*r
        poneerror*=r;
        
        /*System.out.println("R:"+r);
        System.out.println("Size:"+size);
        System.out.println("Zero prob:"+pnoerror*100);
        System.out.println("1 prob:"+(poneerror + poneerror)*100);*/
        
        
        boolean canBe = false;
        for (int i = 0; i < (son.getSize()/11);i++){
            error = randomGen.nextDouble();
            if (error < pnoerror){
            } else if (error < poneerror + pnoerror){
                canBe = true; // one error fine but not really
            }else{
                return 2; // otherwise cannot recover error.
            }
        }
        if (canBe){
            return 1;
        }
        return 0;
        
    }

    private ArrayList<Integer> equalDivisionSuccession(int NOISE_VARIABILITY) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        int min = 100/NOISE_VARIABILITY;
        int value = 0;
        for (int i = 0; i < NOISE_VARIABILITY;i++){
            value+=min;
            list.add(value);
        }
        return list;
    }
   

    
}
