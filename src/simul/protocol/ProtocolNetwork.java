package simul.protocol;

import co.paralleluniverse.fibers.SuspendExecution;
import desmoj.core.dist.ContDistUniform;
import desmoj.core.simulator.Experiment;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeInstant;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import simul.infrastructure.MbusNetwork;

import java.util.Scanner;

/**
 * Created by Federico Falconi on 05/07/2017.
 */

public class ProtocolNetwork extends MbusNetwork {
    private Master master;
    private int slavesNum;
	private Experiment exp;
    

    public ProtocolNetwork(Model owner, String name, boolean showInReport, boolean showInTrace, int slaveNum,
                           double mediumNoise, int variability, int mediumDegreee, int noiseRange, long lasting, int packetDestinationMax) {
        super(owner, name, showInReport, showInTrace, slaveNum + 1, mediumNoise, variability, mediumDegreee,noiseRange, lasting,packetDestinationMax);
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
                    netGraph.getEdgeWeight(edge) * 2 / 10);
        }

        return masterGraph;
    }


    public void init() {}


    public void doInitialSchedules() {
        for (int i = 1; i <= slavesNum; i++) {
            setNode(new Slave(this, "Slave", true, i), i);
        }

        master = new Master(this, true, generateMasterGraph());
        setNode(master, 0);
        master.activate();
    }


    public static void main(String[] args) throws Exception {
        int slaveNum;
        int variability;
        double mediumNoise;
        int mediumDegree;
        int noiseRange;
        long lasting;
        int packetDestinationMax;
        Scanner in = new Scanner(System.in);
        /*slaveNum = Integer.parseInt(args[0]);
        mediumNoise = Double.parseDouble(args[1]);
        variability = Integer.parseInt(args[2]);
        mediumDegree = Integer.parseInt(args[3]);
        packetDestinationMax = Integer.parseInt(args[4]);
        lasting = Integer.parseInt(args[5]);*/
        
        
        
        
        System.out.println("How many slaves ?");
        slaveNum = in.nextInt();
        System.out.println("Which is the noise average?");
        mediumNoise = in.nextDouble();
        System.out.println("Which is the probability that the noise change in a specific time?" );
        variability = in.nextInt();
        System.out.println("How many max arches incedence in a node?" );
        mediumDegree = in.nextInt();
        /*System.out.println("What is the noise range in a node? <= max arches.");
        noiseRange = in.nextInt();*/
        noiseRange = mediumDegree; // no need @deprecated
        /*if (noiseRange>mediumDegree){
        	throw new IllegalArgumentException(" Noise range > max arches.");
        }*/
        System.out.println("How many nodes can be used as a packet destination?");
         packetDestinationMax = in.nextInt();
        System.out.println("How many packets will sends?" );
        lasting = in.nextLong();

        ProtocolNetwork protocol = new ProtocolNetwork(null, "Protocol network first variant",
                true, true, slaveNum, mediumNoise, variability, mediumDegree, noiseRange, lasting, packetDestinationMax);

        Experiment exp = new Experiment("FirstProtocolExperiment");

        protocol.setExp(exp);
        protocol.connectToExperiment(exp);

        //exp.setShowProgressBar(true);
        //exp.stop(new TimeInstant(10000000));
        exp.tracePeriod(new TimeInstant(0), new TimeInstant(100));
        exp.debugPeriod(new TimeInstant(0), new TimeInstant(50));
        
        exp.start();
        

        exp.report();

        System.out.println();
        if (protocol.messagesSent != 0){
        	 System.out.println("The weight's average in the header is: " + (protocol.headerSum / protocol.messagesSent) +
                     "(frames for packet)");
        	 System.out.println("Master message sent:"+protocol.masterSentMessage);
        	 System.out.println("Master message received:"+protocol.masterReceivedMessage);
        	 System.out.println("Bandwidth avg (0 < x < 1) :"+(protocol.avgBandwidth /protocol.masterSentMessage));
        	 
        	 
        }else{
        	System.out.println("No package has been sent from the system; Change the clock");
        }
        protocol.master.log.close();
        protocol.master.band_log.close();
        

        exp.finish();
    }

	private void setExp(Experiment exp) {
		
		this.setExperiment(exp);
	}
}
