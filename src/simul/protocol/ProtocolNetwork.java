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

    public ProtocolNetwork(Model owner, String name, boolean showInReport, boolean showInTrace, int slaveNum,
                           double mediumNoise, int variability, int mediumDegreee, long lasting) {
        super(owner, name, showInReport, showInTrace, slaveNum + 1, mediumNoise, variability, mediumDegreee, lasting);
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


    public static void main(String[] args) {
        int slaveNum;
        int variability;
        double mediumNoise;
        int mediumDegree;
        long lasting;

        Scanner in = new Scanner(System.in);

        System.out.println("How many slaves ?");
        slaveNum = in.nextInt();
        System.out.println("Which is the noise average?");
        mediumNoise = in.nextDouble();
        System.out.println("Which is the probability that the noise change in a specific time?" );
        variability = in.nextInt();
        System.out.println("How many max arches incedence in a node?" );
        mediumDegree = in.nextInt();
        System.out.println("How long the simulation will take?" );
        lasting = in.nextLong();

        ProtocolNetwork protocol = new ProtocolNetwork(null, "Protocol network first variant",
                true, true, slaveNum, mediumNoise, variability, mediumDegree, lasting);

        Experiment exp = new Experiment("FirstProtocolExperiment");

        protocol.connectToExperiment(exp);

        exp.setShowProgressBar(true);
        exp.stop(new TimeInstant(lasting));
        exp.tracePeriod(new TimeInstant(0), new TimeInstant(100));
        exp.debugPeriod(new TimeInstant(0), new TimeInstant(50));

        exp.start();

        exp.report();

        System.out.println();
        if (protocol.messagesSent != 0){
        	 System.out.println("The weight's average in the header is: " + (protocol.headerSum / protocol.messagesSent) +
                     "(frames for packet)");
        }else{
        	System.out.println("No package has been sent from the system; Change the clock");
        }
       

        exp.finish();
    }
}
