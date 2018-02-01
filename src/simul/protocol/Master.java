package simul.protocol;


import desmoj.core.simulator.*;
import co.paralleluniverse.fibers.SuspendExecution;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import simul.cache.masterCache;
import simul.infrastructure.MbusDevice;
import simul.infrastructure.MbusMessage;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Federico Falconi on 04/07/2017.
 */
public class Master extends MbusDevice {
    private int token = 0;
    private int sended = 0;
    private int returned = 0;
    private int fault = 0;
    

    private List<MasterGraphNode> path;
    private SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> graph;

    private Random randomGen = new Random();
    public PrintWriter log;
    public PrintWriter band_log;
    


    public Master(Model owner, Boolean showInTrace, SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> graph) {
        super(owner, "Master", showInTrace, 0);
        this.graph = graph;
        try {
			this.log = new PrintWriter("master.txt", "UTF-8");
			this.band_log = new PrintWriter("bandwidth.txt", "UTF-8");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
                //System.out.println("Packet fault");
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
        NoiseTable currentTable = null;
        MasterGraphNode node2;
        try {
            for (MasterGraphNode node : path) {

                currentTable = tables.pop();

                for (Integer address : currentTable.getEntries().keySet()) {
                	node2 = findNode(address);
                    currentEdge = graph.getEdge(node, node2);
                    if (currentEdge == null){
                    	// Same edge avoid,
                    	continue;
                    }
                    try{
                    	graph.setEdgeWeight(currentEdge, currentTable.getEntries().get(address));
                    }catch(NullPointerException e){
                    	throw e;
                    }
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("The noise table rumor size (" + tables.size() +
                    ") and the path's length (" + path.size() + ") doesn't match.");
        }
        catch(NullPointerException e){
        	//System.out.println(currentTable.getEntries());
        	
        	
        	throw e;
        	//System.out.println("what?");
        }
    }


    public void lifeCycle() throws SuspendExecution {
        boolean correct;
        Request sending;
        TimeInstant timer;
        masterCache cache = new masterCache();
        MasterGraphNode endNode;
        MasterGraphNode masterNode = findNode(0);
        ShortestPathAlgorithm.SingleSourcePaths<MasterGraphNode, DefaultWeightedEdge> dijkstraPaths;
        ArrayDeque<Integer> pathReduced,pathEncoded;
        System.out.println(graph);
        // Generate fixed list node.
        ArrayList<Integer> dest = getDestinations(this.network.configManager.getMaxDest()); // without master.
        Integer fixedNode = dest.get(0);//randomGen.nextInt(graph.vertexSet().size() - 1) +1;
        Integer fixedNodeIndex = -1;
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        // 1;//
        //Integer fixedNode;
        /*
         * come descrivi una pecora brutta ?
		 * bela fuori ...e  bella dentro ...
         */
        System.out.println("Simulation starts");
        
        while (true) {
        	//fixedNode = randomGen.nextInt(graph.vertexSet().size() - 1) +1;
        	if (fixedNodeIndex.equals(dest.size()-1) ){
            	fixedNodeIndex = 0; 
            	fixedNode = dest.get(fixedNodeIndex);
            	
            }else{
            	fixedNodeIndex++;
            	fixedNode = dest.get(fixedNodeIndex);
            }
            dijkstraPaths = new DijkstraShortestPath<MasterGraphNode, DefaultWeightedEdge>(graph).getPaths(masterNode);
            endNode = findNode( fixedNode ); // chooose destination and apply dijkstra
            
            
            if (endNode==null){
            	throw new IllegalArgumentException();
            }
            /**
             * The dijkstraPaths path.
             */
            try{
            	path = dijkstraPaths.getPath(endNode).getVertexList();
            }catch(Exception e){
            	//throw new SUSPEND;
            	//System.out.println("NO PATH DEFINED FOR NODE "+fixedNode);
            	continue; // Maybe happen no path ok.
            	//continue;
            }
            try{
            	path.remove(0);
	        }catch(Exception e){
	        	//System.out.println("No master node "+fixedNode);
	        	throw new IllegalArgumentException();
	        	//continue;
	        	// the fuck?
	        }
            

            /**
             * Prepare packet
             * 1 represents the type of packet
             * token represents the sequence number.
             * 20 payload size.
             * 0 the source 
             * path the list of nodes.
             */
            pathEncoded = encodePath(path);
            pathReduced = cache.reducePath(pathEncoded);
            
            //this.network.avgBandwidth += ((pathReduced.size()*1.0)/pathEncoded.size());
            
            //this.log.println(pathReduced.size()+","+pathEncoded.size()+","+pathEncoded.getLast());
            this.network.masterSentMessage++;
            
            //System.out.println("Path:"+pathEncoded);
            //System.out.println("Path:"+pathReduced);
            
            if (pathEncoded.size() -1 != pathReduced.size() ){
            	this.network.masterCacheHit++;
            }
            
            sending = new Request(1, token++, 20, 0, pathEncoded.getFirst(),pathEncoded.getLast(), pathReduced);
            
            correct = false;
            this.network.hopCount = 1;
            this.network.currentThroughtput = pathReduced.size() + 1; // for the destination node.
            /**
             * Send packet and update rumors node.
             */
            transmit(sending, true);

            /*System.out.println();
            System.out.println();
            System.out.println("Sended " + ++sended + " Received: " + returned + ", Time: " + presentTime());
            System.out.println("Sent packet with header size: " + (sending.getHopList().size() + 4));*/
            
            // wait for a while.
            timer = TimeOperations.add(presentTime(), new TimeSpan(path.size() * 5 * sending.getLength()));
            activate(timer);

            while (true) {
                passivate();
                // wait until i receive something.
                if (getReceived() == null) { // TIMEOUT 
                	// not correct? PACKET LOSS.
                    if (!correct) { // double check not required.
                        //System.out.println("Packet loss");
                        fault++;
                        double avg = this.network.currentThroughtput/(this.network.hopCount+0.0); // (the current node)
                        double bavg = computeSum(pathEncoded.size(),this.network.hopCount)/(this.network.hopCount+0.0);
                        this.network.avgBandwidth += avg;
                        this.network.avgBestBandwidth += bavg;
                        /*if (avg > bavg){
                        	System.out.println("what?");
                        	//throw new Exception("what?");
                        }*/
                        this.band_log.println("'"+formatter.format(avg)+"';"+"'"+formatter.format(bavg)+"';"+fault+";"+pathEncoded.getLast());
                        //System.out.println(this.network.avgBestBandwidth);
                        //System.out.println("best avg:"+this.network.avgBestBandwidth);
                        // This is done for synchronization between the slaves and the master.
                        // It reduces the bandwidth spread but is useful to avoid possible loop or possible unreachable destination due to locked path.
                        System.out.print("Packet #"+this.network.masterSentMessage+" fault "); 
                        System.out.println("Progress: "+((this.network.masterSentMessage*100)/this.network.getLasting())+"%");
                        
                        cache.faultCaching(pathEncoded,fixedNode);
                        if ((pathReduced.size()*1.0)<=pathEncoded.size()){ // avoiding error data.
                        	//this.band_log.println(("'"+formatter.format(((pathReduced.size()*1.0)/pathEncoded.size()) *100))+"';"+fault+";"+pathEncoded.getLast());
                        }
                        
                        
                        // Update the weight of graph.
                        for (DefaultWeightedEdge edge : dijkstraPaths.getPath(endNode).getEdgeList()) {
                            graph.setEdgeWeight(edge, (graph.getEdgeWeight(edge) * 2 + 2) / 3);
                        }
                        // close the simulation if the master sent a specific number of packet.
                        if (this.network.masterSentMessage>=this.network.getLasting()){
                        	this.network.getExperiment().stop();
                        }
                    }

                    break;
                }
                else {
                    if (getReceived().getClass() == Response.class) { // Receive a message..
                        Response received = (Response)retrieveMsg(); // read packet.
                        
                        if (decode(received, graph.getEdge(masterNode, findNode(received.getSource())) )) { // decode the message 
                            if (received.getNextHop() == 0 && received.getNoiseTables().size() == path.size()) {
                                correct = true;
                                //System.out.println("Packet received");
                                double avg = this.network.currentThroughtput/(this.network.hopCount+0.0); // (the current node)
                                double bavg = computeSum(pathEncoded.size(),this.network.hopCount)/(this.network.hopCount+0.0);
                                this.network.avgBandwidth += avg;
                                this.network.avgBestBandwidth += bavg ;
                                //System.out.println(this.network.avgBestBandwidth);
                                //System.out.println(this.network.avgBandwidth);
                                if (avg > bavg){
                                	System.out.println("what?");
                                }
                                System.out.print("Packet #"+this.network.masterSentMessage+" ok     "); 
                                System.out.println("Progress: "+((this.network.masterSentMessage*100)/this.network.getLasting())+"% "+avg);
                                this.band_log.println("'"+formatter.format(avg)+"';"+"'"+formatter.format(bavg)+"';"+fault+";"+pathEncoded.getLast());
                                //System.out.println("best avg:"+this.network.avgBestBandwidth);
                                //this.band_log.println(("'"+formatter.format(((pathReduced.size()*1.0)/pathEncoded.size())*100))+"';"+fault+";"+pathEncoded.getLast());
                                
                                /*if (pathReduced.size() +1 == pathEncoded.size()){
                                	// the first time caching.
                                	// The reduce procedure removes the last node.
                                	// The last node is introduced here
                                	pathReduced.add(fixedNode);
                                }*/
                                
                                cache.applyCaching(pathEncoded,fixedNode);
                                readNoiseTables(received.getNoiseTables());
                                this.network.masterReceivedMessage++;
                                returned++;
                                if (this.network.masterSentMessage>=this.network.getLasting()){
                                	this.network.getExperiment().stop();
                                }
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


	private Integer computeSum(int i,int limit) {
		// TODO Auto-generated method stub
		int sum = 0;
		for (int y = 0; y < limit && i > 0; y++){
			// sum until reach limit number.
			sum+=i;
			i--;
		}
		return sum;
		//return null;
	}


	private ArrayList<Integer> getDestinations(int maxDest) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		Integer i = 0;
		int node;
		/*do{
			node = randomGen.nextInt(graph.vertexSet().size() - 1);
			if (node == 0){
				continue;
			}
			if (list.contains(node)){
				continue;
			}
			list.add(node);
		}while(list.size()!=maxDest);*/
		do{
			i++;
			node = i;
			if (node == 0){
				continue;
			}
			if (list.contains(node)){
				continue;
			}
			list.add(node);
		}while(list.size()!=maxDest);
		return list;
	}
}
