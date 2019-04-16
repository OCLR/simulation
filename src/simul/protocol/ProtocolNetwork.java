package simul.protocol;

import co.paralleluniverse.fibers.SuspendExecution;
import desmoj.core.dist.ContDistUniform;
import desmoj.core.simulator.Experiment;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeInstant;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.Math.random;
import java.util.Random;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import simul.infrastructure.MbusNetwork;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import simul.base.NetConfigManager;

/**
 * Created by Federico Falconi on 05/07/2017.
 */

public class ProtocolNetwork extends MbusNetwork {
    private Master master;
    private int slavesNum;
    private Experiment exp;
    

    public ProtocolNetwork(Model owner, String name, boolean showInReport, boolean showInTrace, int slaveNum,
                           int powerNoisePerc, int variability, int mediumDegreee, int noiseRange, long lasting, int packetDestinationMax) {
        super(owner, name, showInReport, showInTrace, slaveNum, powerNoisePerc, variability, mediumDegreee,noiseRange, lasting,packetDestinationMax);
        this.slavesNum = slaveNum;
    }

    public String description() {
        /*return "Questo modello descrive il protocollo per utility network " +
                "proposto dal professor Culmone. Quindi il routing si basa su " +
                "una sequenza di hop codificata nell'header del pacchetto.";*/
        return " This model describes a protocol for utility network, proposed by prof. Culmone." +
        		" Therefore the routing is based on a precomputed sequence encoded with the packet header from master";
    }


    public SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> generateMasterGraph() {
        SimpleWeightedGraph<Integer, DefaultWeightedEdge> netGraph = getGraphRepresentation();
        SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> masterGraph =
                new SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge>(DefaultWeightedEdge.class);

        MasterGraphNode edgeSource;
        MasterGraphNode edgeTarget;
        for (DefaultWeightedEdge edge : netGraph.edgeSet()) {
           edgeSource = new MasterGraphNode(netGraph.getEdgeSource(edge)); 
           edgeTarget = new MasterGraphNode(netGraph.getEdgeTarget(edge));

           masterGraph.addVertex(edgeSource);
           masterGraph.addVertex(edgeTarget);

            masterGraph.setEdgeWeight(masterGraph.addEdge(edgeSource, edgeTarget),
                   NetConfigManager.updateSingleNoise());
        }

        return masterGraph;
    }


    public void init() {}


    public void run() throws SuspendExecution, ExecutionException, TimeoutException, InterruptedException, IOException, Exception {
        for (int i = 1; i < slavesNum; i++) {
            setNode(new Slave(this,"Slave", true, i), i);
        }
        System.out.println("Building Network");
        SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> graph = generateMasterGraph();
        System.out.println("Network created");
        
        master = new Master(this, true, graph);
        setNode(master, 0);
        master.lifeCycle();
    }


    public static void main(String[] args) throws Exception {
        int slaveNum;
        int variability;
        int powerNoisePerc = 0;
        int mediumDegree;
        int noiseNodesPercentage;
        long lasting;
        int packetDestinationMax;
        Scanner in = new Scanner(System.in);
        if (args.length == 6){
	        slaveNum = Integer.parseInt(args[0]);
	        powerNoisePerc = Integer.parseInt(args[1]);
	        variability = Integer.parseInt(args[2]);
                noiseNodesPercentage = Integer.parseInt(args[3]);
	        mediumDegree = Integer.parseInt(args[4]);
                lasting = Integer.parseInt(args[5]);
	        //packetDestinationMax = Integer.parseInt(args[4]);
	        packetDestinationMax = slaveNum;
                
        }else{
        	System.out.println("How many slaves ?");
            slaveNum = in.nextInt();
            System.out.println("What is the noise probability in this network?");
            powerNoisePerc = in.nextInt();
            System.out.println("Which is the noise packet frequency?" );
            variability = in.nextInt();
            System.out.println("Which is the noise probability impact?" );
            noiseNodesPercentage = in.nextInt();
            System.out.println("How many max arches incedence in a node?" );
            mediumDegree = in.nextInt();
            
            //System.out.println("How many nodes can be used as a packet destination?");
            //packetDestinationMax = in.nextInt();
            // packetDestinationMax = 1;
            packetDestinationMax = slaveNum;
            System.out.println("How many packets will sends?" );
            lasting = in.nextLong();
            /*System.out.println("What is the noise range in a node? <= max arches.");
            noiseRange = in.nextInt();*/
        }
        
        Random r = new Random();
        Stats.statFile = "distanceNoise-"+slaveNum+"-"+powerNoisePerc+"-"+variability+"-"+noiseNodesPercentage+"-"+mediumDegree+"-"+lasting+".txt";
        Stats.statFile2 = "fault-"+slaveNum+"-"+powerNoisePerc+"-"+variability+"-"+noiseNodesPercentage+"-"+mediumDegree+"-"+lasting+".txt";
        
        Stats.updateMasterSlave = 0;
        Stats.updateNoiseSlave = 0;
        Stats.windowFault = 0;
        /*
        System.out.println("How many slaves ?");
        slaveNum = in.nextInt();
        System.out.println("Which is the noise average?");
        mediumNoise = in.nextDouble();
        System.out.println("Which is the probability that the noise change in a specific time?" );
        variability = in.nextInt();
        System.out.println("How many max arches incedence in a node?" );
        mediumDegree = in.nextInt();*/
        /*System.out.println("What is the noise range in a node? <= max arches.");
        noiseRange = in.nextInt();*/
        /*if (noiseRange>mediumDegree){
        	throw new IllegalArgumentException(" Noise range > max arches.");
        }*/
        /*System.out.println("How many nodes can be used as a packet destination?");
         packetDestinationMax = in.nextInt();
        System.out.println("How many packets will sends?" );
        lasting = in.nextLong();*/

        ProtocolNetwork protocol = new ProtocolNetwork(null, "Protocol network first variant",
                true, true, slaveNum, powerNoisePerc, variability, mediumDegree, noiseNodesPercentage, lasting, packetDestinationMax);

        //Experiment exp = new Experiment("FirstProtocolExperiment");

        //protocol.setExp(exp);
        //protocol.connectToExperiment(exp);

        //exp.setShowProgressBar(true);
        //exp.stop(new TimeInstant(10000000));
        /*exp.tracePeriod(new TimeInstant(0), new TimeInstant(100));
        exp.debugPeriod(new TimeInstant(0), new TimeInstant(50));
        
        exp.start();
        exp.report();*/

        protocol.run();
        
        PrintWriter out = null;
        if (Stats.masterSentMessage != 0){
        	/* System.out.println("The weight's average in the header is: " + (protocol.headerSum / protocol.messagesSent) +
                     "(frames for packet)");*/
        	 System.out.println("Master message sent:"+Stats.masterSentMessage);
        	 System.out.println("Master message received:"+Stats.masterReceivedMessage);
                 
        	 System.out.println("Master Cache Hit:"+ Stats.masterCacheHit);
        	 System.out.println("Master Cache Impact:"+ ((Stats.masterCacheHit/(Stats.masterSentMessage+0.0))*100));
        	 System.out.println("Bandwidth Cache   case avg :"+(Stats.avgBandwidth /Stats.masterSentMessage));
        	 System.out.println("Bandwidth Default case avg  :"+(Stats.avgBestBandwidth /Stats.masterSentMessage));
        	 System.out.println("Comparison index (0 < x < 1) :"+(Stats.avgBandwidth /Stats.masterSentMessage)/(Stats.avgBestBandwidth /Stats.masterSentMessage));
                 double bandw = (Stats.avgBandwidth /Stats.masterSentMessage)/(Stats.avgBestBandwidth /Stats.masterSentMessage);
                try{
                    FileWriter fw = new FileWriter("results.txt", true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    out = new PrintWriter(bw);
                    out.print(slaveNum);
                    out.print(";");
                    out.print(powerNoisePerc);
                    out.print(";");
                    out.print(variability);
                    out.print(";");
                    out.print(noiseNodesPercentage);
                    out.print(";");
                    out.print(mediumDegree);
                    out.print(";");
                    out.print(lasting);
                    out.print(";");
                    out.print(Stats.masterReceivedMessage);
                    out.print(";");
                    out.print((Stats.masterReceivedMessage/(Stats.masterSentMessage+0.0))*100);
                    out.print(";");
                    out.print(Stats.masterCacheHit);
                    out.print(";");
                    out.print((Stats.masterCacheHit/(Stats.masterSentMessage+0.0))*100);
                    out.print(";");
                    out.print((Stats.avgBandwidth /Stats.masterSentMessage));
                    out.print(";");
                    out.print((Stats.avgBestBandwidth /Stats.masterSentMessage));
                    out.print(";");
                    out.print(bandw*100);
                    out.print(";");
                    out.print(Stats.masterAvgLength/Stats.masterSentMessage);
                    
                    out.println();
                    //more code
                } catch (IOException e) {
                    //exception handling left as an exercise for the reader
                    throw e;
                }
        	 
        }else{
        	System.out.println("No package has been sent from the system; Change the clock");
        }
        out.close();
        //protocol.master.log.close();
        //protocol.master.log_fault.close();
        //.master.band_log.close();
    }

}
