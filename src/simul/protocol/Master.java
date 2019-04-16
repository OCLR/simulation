package simul.protocol;

import desmoj.core.simulator.*;
import co.paralleluniverse.fibers.SuspendExecution;
import java.io.BufferedWriter;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import simul.cache.masterCache;
import simul.infrastructure.MbusDevice;
import simul.infrastructure.MbusMessage;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import simul.base.NetConfigManager;
import simul.infrastructure.MbusNetwork;

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

    public PrintWriter log;
    public PrintWriter band_log;

    private boolean correct;
    private Request sending;
    private TimeInstant timer;
    private masterCache cache = new masterCache();
    private MasterGraphNode endNode;
    private ShortestPathAlgorithm.SingleSourcePaths<MasterGraphNode, DefaultWeightedEdge> dijkstraPaths;
    private ArrayDeque<Integer> pathEncoded;
    private Object pathReduced;
    private MasterGraphNode masterNode;
    private DecimalFormat formatter = new DecimalFormat("#,###.00");
    private Integer fixedNode;
    public PrintWriter log_fault;

    public Master(MbusNetwork owner, Boolean showInTrace, SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> graph) {
        super(owner, "Master", showInTrace, 0);
        this.graph = graph;
        /*try {
            //this.log = new PrintWriter(Stats.statFile, "UTF-8");
            //this.log_fault = new PrintWriter(Stats.statFile2, "UTF-8");       
            //this.band_log = new PrintWriter("bandwidth.txt", "UTF-8");

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
    }

    /**
     *
     * @param message
     * @return
     * @throws SuspendExecution
     * @throws CommunicationFault
     */
    public boolean decode(MbusMessage message) {

        /*        if ((message.getErrors(1) < 3) && (message.getErrors(2) < 3)) {
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
        }*/
        //System.out.println("Error Rate:"+message.getErrorRate());
      /*  if (message.getErrorRate() < 0) {
            return false;
        }*/
        Set<DefaultWeightedEdge> list = this.graph.edgesOf(masterNode);
        
        for (DefaultWeightedEdge ed: list){
            if (graph.getEdgeTarget(ed).equals(message.getSource())){
                this.graph.setEdgeWeight(ed, message.getErrorRate()==2?Integer.MAX_VALUE:message.getErrorRate());
                break;
            }
        }
        //System.out.println(message.getErrorRate());
        // Detect if the node is a response or a request.
        //   Response resp = (Response) message;
        /*DefaultWeightedEdge edge = graph.getEdge(findNode(message.getSource()), this.masterNode);
        DefaultWeightedEdge edge2 = graph.getEdge(this.masterNode,findNode(message.getSource()));
        
        DefaultWeightedEdge fedge = (edge==null)?edge2:edge; */
        
        
        // Request req = (Request) message;

        /*if (message.getErrorRate() != 2) {
            throw new CommunicationFault(0);
        }*/
        
        return true;
    }

    private MasterGraphNode findNode(int nodeAddress) {
        for (MasterGraphNode vertex : graph.vertexSet()) {
            if (vertex.getStaticAddress() == nodeAddress) {
                return vertex;
            }
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
        double error;
        try {

            for (MasterGraphNode node : path) {
                if (tables.size()==0){
                    return;
                }
                currentTable = tables.pop();

                for (Integer address : currentTable.getEntries().keySet()) {
                    node2 = findNode(address);
                    currentEdge = graph.getEdge(node, node2);
                    if (currentEdge == null) {
                        // Same edge avoid,
                        continue;
                    }
                    error = currentTable.getEntries().get(address);
                    if (error == 2){
                        error = Double.MAX_VALUE;
                    }
                    try {
                        Stats.updateMasterSlave++;
                        graph.setEdgeWeight(currentEdge, error);
                    } catch (NullPointerException e) {
                        throw e;
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("The noise table rumor size (" + tables.size()
                    + ") and the path's length (" + path.size() + ") doesn't match.");
        } catch (NullPointerException e) {
            //System.out.println(currentTable.getEntries());

            throw e;
            //System.out.println("what?");
        }
    }

    public void lifeCycle() throws SuspendExecution, ExecutionException, TimeoutException, InterruptedException, IOException, Exception {

        Master m = this;
        masterNode = findNode(0);
        // Generate fixed list node.
        ArrayList<Integer> dest = getDestinations(this.network.configManager.getMaxDest()); // without master.
        fixedNode = dest.get(0);//randomGen.nextInt(graph.vertexSet().size() - 1) +1;
        Integer fixedNodeIndex = -1;
        ExecutorService pool = Executors.newFixedThreadPool(5);

        // 1;//
        //Integer fixedNode;
        /*
         * come descrivi una pecora brutta ?
		 * bela fuori ...e  bella dentro ...
         */
        System.out.println("Simulation starts");

        while (true) {
            //fixedNode = randomGen.nextInt(graph.vertexSet().size() - 1) +1;
            if (fixedNodeIndex.equals(dest.size() - 1)) {
                fixedNodeIndex = 0;
                fixedNode = dest.get(fixedNodeIndex);

            } else {
                fixedNodeIndex++;
                fixedNode = dest.get(fixedNodeIndex);
            }
            dijkstraPaths = new DijkstraShortestPath<MasterGraphNode, DefaultWeightedEdge>(graph).getPaths(masterNode);
            endNode = findNode(fixedNode); // chooose destination and apply dijkstra

            if (endNode == null) {
                throw new IllegalArgumentException();
            }
            /**
             * The dijkstraPaths path.
             */
            try {
                path = dijkstraPaths.getPath(endNode).getVertexList();
            } catch (Exception e) {
                //throw new SUSPEND;
                //System.out.println("NO PATH DEFINED FOR NODE "+fixedNode);
                continue; // Maybe happen no path ok.
                //continue;
            }

            try {
                path.remove(0);
            } catch (Exception e) {
                //System.out.println("No master node "+fixedNode);
                throw new IllegalArgumentException();
                //continue;
                // the fuck?
            }

            /**
             * Prepare packet 1 represents the type of packet token represents
             * the sequence number. 20 payload size. 0 the source path the list
             * of nodes.
             */
            pathEncoded = encodePath(path);
            pathReduced = cache.reducePath(pathEncoded);
            Stats.masterAvgLength += pathEncoded.size();
            //this.network.avgBandwidth += ((pathReduced.size()*1.0)/pathEncoded.size());
            //this.log.println(pathReduced.size()+","+pathEncoded.size()+","+pathEncoded.getLast());
            

            //System.out.println("Path:"+pathEncoded);
            //System.out.println("Path:"+pathReduced);
            
            //if (pathReduced instanceof ArrayList) {
                
            //}
            
            if ( pathReduced instanceof  ArrayList){
                Stats.masterCacheHit++;
            }else{
                ArrayDeque<Integer> pathRed = (ArrayDeque) pathReduced;
                if (pathRed.size()+1!=pathEncoded.size()){
                    Stats.masterCacheHit++;
                }
            }

            sending = new Request(1, token++, 20, 0, pathEncoded.getFirst(), pathEncoded.getLast(), pathReduced);

            correct = false;
            //sending. = ; // for the destination node.
            //sending.incThroughput(pathReduced.size() + 1);
            Stats.currentThroughtput = 0 ;
            Stats.hopCount = 0;
            //System.out.println(pathEncoded);
            
           // ExecutorService service = Executors.newSingleThreadExecutor();

            /*Runnable r = new masterCommunication(sending) {

                    @Override
                    public void run() {
                        
                        
                    }
                };*/
            Response answer = null;
            // update master number of messages sent.
            Stats.masterSentMessage+=1;
            
            try {

               answer = m.transmit(sending, true); // Going down.

               if (answer.getCode() >= 0) { // Error rate.
                   m.receive(fixedNode, pathEncoded, pathReduced, answer);
               } else {
                   m.fault(fixedNode, pathEncoded, pathReduced, answer, true);
               }
                 int fault = Stats.windowFault;
                 int nMaster = Stats.updateMasterSlave;
                 int nSlave = Stats.updateNoiseSlave;
                 if (m.network.updateNoise()){
                   // this.log.println(nMaster+","+nSlave+","+fault);
                 }
               
               // Check for fault.
             } catch (CommunicationFault ex) {
                 
                 m.timeout(fixedNode, pathEncoded, pathReduced, answer);
                 
                 if (!ex.isUnRecoverable()){
                    readNoiseTables(ex.getTables());
                 }
                 Stats.windowFault++;
                 int fault = Stats.windowFault;
                 int nMaster = Stats.updateMasterSlave;
                 int nSlave = Stats.updateNoiseSlave;
                 
                 if (m.network.updateNoise()){
                   // this.log.println(nMaster+","+nSlave+","+fault);
                 }
             }
//            MasterCommunication masterCommunication = new MasterCommunication(m, fixedNode, pathEncoded, pathReduced, sending);
//            pool.execute(masterCommunication);
        
           
            
            // Need to update noise table for all nodes.
            //this.network.updateLocalNoise(new ArrayList( Arrays.asList(pathEncoded.toArray())  ));
            
            
            
            // Future<?> f = service.submit(r);

            // f.get(2, TimeUnit.MINUTES);     // attempt the task for two minutes
            if (Stats.masterSentMessage >= this.network.getLasting()) {
                return; // finish simulation.
            }
  

         }
    }

    private Integer computeSum(int i, int limit) {
        // TODO Auto-generated method stub
        int sum = 0;
        for (int y = 0; y < limit && i > 0; y++) {
            // sum until reach limit number.
            sum += i;
            i--;
        }
        return sum;
        //return null;
    }

    private ArrayList<Integer> getDestinations(int maxDest) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        Random randomGen = new Random();
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
        do {
            i++; // 0..99 ?!
            node = i;//randomGen.nextInt(graph.vertexSet().size() - 1 );
            if (node == 0) {
                continue;
            }
            if (list.contains(node)) {
                continue;
            }
            list.add(node);
        } while (list.size() != maxDest -1);
        Collections.shuffle(list);
        return list;
    }

    public void timeout(Integer fixedNode,ArrayDeque<Integer>  pathEncoded,Object  pathReduced,Response msg) {
        this.fault(fixedNode,pathEncoded,pathReduced,msg,false); // treat it like a fault (for now). With no noise table 
        Stats.fault++;
//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void receive(Integer fixedNode,ArrayDeque<Integer>  pathEncoded,Object  pathReduced,Response msg) {
        
        
        Response message = (Response) msg;
        
        
        //if (message.getNoiseTables().size() == path.size()) {
            
            //System.out.println("Packet received");
            double avg = Stats.currentThroughtput / (Stats.hopCount + 0.0); // (the current node)
            double bavg = computeSum(pathEncoded.size(), Stats.hopCount) / (Stats.hopCount + 0.0);
            Stats.avgBandwidth += avg;
            Stats.avgBestBandwidth += bavg;
            //System.out.println(this.network.avgBestBandwidth);
            //System.out.println(this.network.avgBandwidth);
            if (avg > bavg) {
             throw new InternalError();
            ///System.out.println("what?");
          }
            System.out.print("Packet #" + Stats.masterSentMessage + " ok     ");
            System.out.println("Progress: " + ((Stats.masterSentMessage * 100) / this.network.getLasting()) + "% " + (Stats.avgBandwidth /Stats.masterSentMessage)/(Stats.avgBestBandwidth /Stats.masterSentMessage));
            //this.log_fault.println("0,"+((Stats.masterReceivedMessage /(Stats.masterSentMessage+0.0)))+"");
            //this.band_log.println("'" + formatter.format(avg) + "';" + "'" + formatter.format(bavg) + "';" + fault + ";" + pathEncoded.getLast());
            //System.out.println("best avg:"+this.network.avgBestBandwidth);
            //this.band_log.println(("'"+formatter.format(((pathReduced.size()*1.0)/pathEncoded.size())*100))+"';"+fault+";"+pathEncoded.getLast());

            /*if (pathReduced.size() +1 == pathEncoded.size()){
                                	// the first time caching.
                                	// The reduce procedure removes the last node.
                                	// The last node is introduced here
                                	pathReduced.add(fixedNode);
                                }*/
            cache.applyCaching(pathEncoded, fixedNode);
            
            if (message!=null){
                readNoiseTables(message.getNoiseTables());
            }
            
            Stats.masterReceivedMessage++;
            //returned++;
       // }
    }

    public void fault(Integer fixedNode,ArrayDeque<Integer>  pathEncoded,Object  pathReduced,Response message,boolean nt) {
        Stats.fault++;
        double avg;
        double bavg;
        if (Stats.hopCount == 0){
            avg = 0;
            bavg =0;
        }else{
            avg =  Stats.currentThroughtput / (Stats.hopCount + 0.0); // (the current node)
            bavg = computeSum(pathEncoded.size(), Stats.hopCount) / (Stats.hopCount + 0.0);
        }
        
        Stats.avgBandwidth += avg;
        Stats.avgBestBandwidth += bavg;
         if (avg > bavg) {
             throw new InternalError();
            ///System.out.println("what?");
         }
        //this.log_fault.println("1,"+((Stats.masterReceivedMessage /(Stats.masterSentMessage + 0.0)))+"");
        //this.band_log.println("'" + formatter.format(avg) + "';" + "'" + formatter.format(bavg) + "';" + fault + ";" + pathEncoded.getLast());
        //System.out.println(this.network.avgBestBandwidth);
        //System.out.println("best avg:"+this.network.avgBestBandwidth);
        // This is done for synchronization between the slaves and the master.
        // It reduces the bandwidth spread but is useful to avoid possible loop or possible unreachable destination due to locked path.
        System.out.print("Packet #" + Stats.masterSentMessage + " fault ");
        System.out.println("Progress: " + ((Stats.masterSentMessage * 100) / this.network.getLasting()) + "%"+(Stats.avgBandwidth /Stats.masterSentMessage)/(Stats.avgBestBandwidth /Stats.masterSentMessage));
        //this.log.println("1,"+((Stats.avgBandwidth /Stats.masterSentMessage)/(Stats.avgBestBandwidth /Stats.masterSentMessage))+"");
        cache.faultCaching(pathEncoded, this.fixedNode);

        
        if (nt){
            readNoiseTables(message.getNoiseTables());
        }
        /*for (DefaultWeightedEdge edge : dijkstraPaths.getPath(this.endNode).getEdgeList()) {
            //graph.setEdgeWeight(edge, (graph.getEdgeWeight(edge) * 2 + 2) / 3);
            graph.setEdgeWeight(edge, NetConfigManager.getValidNoise((graph.getEdgeWeight(edge) + 1)));
        }*/
        /// close the simulation if the master sent a specific number of packet.

        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
