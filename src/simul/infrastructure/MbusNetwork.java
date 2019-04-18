package simul.infrastructure;

import desmoj.core.simulator.*;
import java.util.ArrayList;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import simul.base.NetConfigManager;

import java.util.HashMap;
import simul.protocol.Slave;
import simul.protocol.Stats;

/**
 * Created by Federico Falconi on 04/07/2017.
 */

public abstract class MbusNetwork  {
    private MbusDevice nodes[];
    public NetConfigManager configManager;
    private long lasting;

    protected double headerSum = 0;
    protected long messagesSent = 0; // number of package sent.
    public double avgBandwidth  = 0; // number of package sent.
    public long masterSentMessage = 0;
    public long masterReceivedMessage = 0;
    private Experiment exp;
    public long masterCacheHit = 0;
	


    public MbusNetwork(Model owner, String name, boolean showInReport, boolean showInTrace, int nodesNum,
                       int powerNoiseNodePercentage, int variability, int mediumDegree, int noiseEdgesNodePercentage, long lasting, int packetDestinationMax) {
        //super(owner, name, showInReport, showInTrace);
        nodes = new MbusDevice[nodesNum];
        this.lasting = lasting;
        configManager = new NetConfigManager(nodesNum, variability, powerNoiseNodePercentage, mediumDegree, noiseEdgesNodePercentage,packetDestinationMax);
    }


    public synchronized HashMap<Integer, Double> getOutgoingEdges(int source) {
        return configManager.getOutgoingEdges(source);
    }

    public synchronized void incMSentMessage() {
        
        if (this.masterSentMessage < this.getLasting()){
            this.masterSentMessage++;
        }
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

    public synchronized boolean updateNoise() {return configManager.updateNoise(Stats.masterSentMessage);}

    public void updateLocalNoise(ArrayList<Integer> nodes) {
        for (int i = 0; i < nodes.size();i++){
            Slave s = (Slave) this.nodes[nodes.get(i)];
            s.updateLocalNoise();
        }
    }


    protected SimpleWeightedGraph<Integer, DefaultWeightedEdge> getGraphRepresentation() {
        return configManager.getGraphRepresentation();
    }


    protected void setNode(MbusDevice node, int pos) {
        nodes[pos] = node;
    }

    public synchronized long getMasterSentNode() {
        return this.masterSentMessage;
    }
}
