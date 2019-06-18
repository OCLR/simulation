package simul.infrastructure;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import simul.nodes.MbusDevice;
import simul.nodes.Master;
import simul.nodes.MasterGraphNode;
import simul.nodes.Slave;
import simul.protocol.SimulationConfiguration;
import simul.protocol.Stats;

/**
 * Created by Federico Falconi on 04/07/2017.
 */

public class MbusNetwork  {
    private MbusDevice nodes[];
    private Stats stats;
    // public NetConfigManager configManager;
    private long lasting;

    public float getBer() {
        return ber;
    }

    private float ber;



    private int slavesNum;
    private Master master;
    private SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> networkGraph;


    public MbusNetwork( String filename, float ber, long lasting) throws IOException {
        //super(owner, name, showInReport, showInTrace);
        this.ber = ber;
        this.lasting = lasting;
        this.stats = new Stats();
        this.create(filename);


    }

    public int getSlavesNum() {
        return slavesNum;
    }

    protected void setNode(MbusDevice node, int pos) {
        nodes[pos] = node;
    }

    public MbusDevice getNode(int pos) {
        return nodes[pos];
    }

    public long getLasting() {
        return lasting;
    }

    // public synchronized boolean updateNoise() {return configManager.updateNoise(Stats.masterSentMessage);}

    /*public void updateLocalNoise(ArrayList<Integer> nodes) {
        for (int i = 0; i < nodes.size();i++){
            Slave s = (Slave) this.nodes[nodes.get(i)];
            s.updateLocalNoise();
        }
    }*/

    public synchronized Set<DefaultWeightedEdge> getOutgoingEdges(int source) {
        return this.networkGraph.edgesOf(new MasterGraphNode(source));
    }

    public void init() {}

    /**
     * Create the network
     */
    private void create(String filename) throws IOException {


        System.out.println("Building Network from file "+filename);
        this.networkGraph = this.createGraphFromFile(filename);
        this.nodes = new MbusDevice[slavesNum+1];
        for (int i = 1; i <= slavesNum; i++) {
            this.setNode(new Slave(this, i), i);
        }
        SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> masterGraph = this.generateMasterGraph(this.networkGraph);
        this.master = new Master(this,masterGraph);
        setNode(master, 0);
        System.out.println("Network created");
    }

    private SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> generateMasterGraph(SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> networkGraph){
        SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> masterGraph = (SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge>) networkGraph.clone();
        Set<DefaultWeightedEdge> edges =  masterGraph.edgeSet();
        //
        for (DefaultWeightedEdge edge: edges){
            masterGraph.setEdgeWeight(edge,ECC.RESET);
        }
        return masterGraph;
    }

    private SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> createGraphFromFile(String filename)  throws IOException {
        // Reading file.
        String file = this.readFile(filename);
        // Split string in bytes.
        String[] adlist = file.split(System.getProperty("line.separator"));
        String adlistone;
        String[] Snodes;
        int NumberOfNodes = 0;
        int node;
        SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> networkGraph = new SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        // for each string.
        for (int i = 0; i < adlist.length; i++) {
            // foreach string separation.
            adlistone = adlist[i];
            Snodes = adlistone.split(",");
            if (Snodes.length == 0){
                System.out.println("Empty line "+i);
                continue;
            }

            if (Snodes.length == 1){
                System.out.println("Line "+i+ " with only one element");
                continue;
            }

            MasterGraphNode source = new MasterGraphNode(Integer.parseInt(Snodes[0]));
            networkGraph.addVertex(source);

            for (int j = 1; j < Snodes.length; j++) {
                node = Integer.parseInt(Snodes[j]);
                if (node > NumberOfNodes) {
                    NumberOfNodes = node;
                }
                MasterGraphNode dest = new MasterGraphNode(node);
                // Create vertex.
                networkGraph.addVertex(dest);
                System.out.println("Create link: source to dest: "+source+"->"+dest);
                DefaultWeightedEdge edge = networkGraph.addEdge(source,dest);
                if (edge==null){
                    System.out.println("Link from source to dest already created:"+source+"->"+dest);
                }else{
                    networkGraph.setEdgeWeight(edge,this.ber);
                }

            }


        }
        this.slavesNum = NumberOfNodes;
        return networkGraph;
    }


    private String readFile(String filename)  throws IOException{
        File file = new File(filename);
        byte [] fileBytes = Files.readAllBytes(file.toPath());
        String s="";

        for(byte b : fileBytes) {
            s += (char) b;
        }
        return s;
    }
    /**
     * Run the master node.
     * @throws ExecutionException
     * @throws TimeoutException
     * @throws InterruptedException
     * @throws IOException
     * @throws Exception
     */
    public void run() throws ExecutionException, TimeoutException, InterruptedException, IOException, Exception {
        this.master.lifeCycle();
    }


    public SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> getNetworkGraph() {
        return this.networkGraph;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public void printResults() {
        this.getStats().printResults(this.getBer());
    }
}
