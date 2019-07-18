package simul.infrastructure;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import simul.nodes.Master;
import simul.nodes.MasterGraphNode;
import simul.nodes.MbusDevice;
import simul.nodes.Slave;
import simul.protocol.NetworkStats;
import simul.protocol.ResultTable;
import simul.protocol.Stats;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class WMbusNetwork {
    private MbusDevice nodes[];
    private Stats stats;
    // public NetConfigManager configManager;
    private long lasting;
    private int slavesNum;
    private Master master;
    private SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> eccGraph;
    private SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> distanceGraph;


    public WMbusNetwork(SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> distanceGraph,long lasting) throws IOException {
        //super(owner, name, showInReport, showInTrace);
        this.distanceGraph = distanceGraph;
        this.create();
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


    public synchronized Set<DefaultWeightedEdge> getOutgoingEdges(int source) {
        return this.eccGraph.edgesOf(new MasterGraphNode(source));
    }

    /**
     * Create the network
     */
    private void create(){
        this.eccGraph = (SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge>) this.distanceGraph.clone();
        this.eccGraph = this.resetNetwork(this.eccGraph);
        this.generateNetwork(this.distanceGraph);
        this.master = new Master(this,this.eccGraph);
        setNode(master, 0);

        //System.out.println("Network created");
    }

    private void generateNetwork(SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> distanceGraph) {
        int node = 0;
        for (MasterGraphNode n: distanceGraph.vertexSet()) {
            if (n.getStaticAddress() != 0){
                this.setNode(new Slave(this,node),node);
            }
            node++;
        }
    }

    private SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> resetNetwork(SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> networkGraph){
        SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> masterGraph = (SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge>) networkGraph.clone();
        Set<DefaultWeightedEdge> edges =  masterGraph.edgeSet();
        //
        for (DefaultWeightedEdge edge: edges){
            masterGraph.setEdgeWeight(edge,ECC.RESET);
        }
        return masterGraph;
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


    public SimpleWeightedGraph<MasterGraphNode, DefaultWeightedEdge> getEccGraph() {
        return this.eccGraph;
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

    public String printNetworkParametersHeader( boolean csv ) {
        NetworkStats n = new NetworkStats(this);
        ResultTable r = n.printResults();
        String header = r.printHeader(csv);
        return header;
    }

    public String printNetworkSimulationHeader( boolean csv ) {
        ResultTable r = this.getStats().printResults(this.getBer());
        String header = r.printHeader(csv);
        return header;
    }

    public String printNetworkParametersValue( boolean csv ) {
        NetworkStats n = new NetworkStats(this);
        ResultTable r = n.printResults();
        String header = r.printValues(csv);
        return header;
    }

    public String printNetworkSimulationValue( boolean csv ) {
        ResultTable r = this.getStats().printResults(this.getBer());
        String header = r.printValues(csv);
        return header;
    }

    public double getBer(int source, int destination) {
        // get distance from graph.
        // compute ber from distance
        // return ber.
        DefaultWeightedEdge ed = this.distanceGraph.getEdge(new MasterGraphNode(source),new MasterGraphNode(destination));
        Double distance = this.distanceGraph.getEdgeWeight(ed);

    }
}
