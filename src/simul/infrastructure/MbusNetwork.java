package simul.infrastructure;

import desmoj.core.simulator.*;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import simul.base.NetConfigManager;

import java.util.HashMap;

/**
 * Created by Federico Falconi on 04/07/2017.
 */

public abstract class MbusNetwork extends Model {
    private MbusDevice nodes[];
    public NetConfigManager configManager;
    private long lasting;

    protected double headerSum = 0;
    protected long messagesSent = 0; // number of package sent.
    public double avgBandwidth  = 0; // number of package sent.
    public long masterSentMessage = 0;
    public long masterReceivedMessage = 0;
	private Experiment exp;


    public MbusNetwork(Model owner, String name, boolean showInReport, boolean showInTrace, int nodesNum,
                       double mediumNoise, int variability, int mediumDegree, int noiseRange, long lasting, int packetDestinationMax) {
        super(owner, name, showInReport, showInTrace);
        nodes = new MbusDevice[nodesNum];
        this.lasting = lasting;
        configManager = new NetConfigManager(nodesNum, variability, mediumNoise, mediumDegree, noiseRange,packetDestinationMax);
    }


    public HashMap<Integer, Double> getOutgoingEdges(int source) {
        return configManager.getOutgoingEdges(source);
    }


    public MbusDevice getNode(int pos) {
        return nodes[pos];
    }


    public long getLasting() {
        return lasting;
    }

    protected void setExperiment(Experiment exp) {
        this.exp = exp;
    }
    
    public Experiment getExperiment() {
        return  this.exp;
    }

    public void updateNoise() {configManager.updateNoise();}


    protected SimpleWeightedGraph<Integer, DefaultWeightedEdge> getGraphRepresentation() {
        return configManager.getGraphRepresentation();
    }


    protected void setNode(MbusDevice node, int pos) {
        nodes[pos] = node;
    }
}
