package org.wmbus.protocol.nodes;

//import desmoj.core.simulator.*;
//import co.paralleluniverse.fibers.SuspendExecution;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.pmw.tinylog.Logger;
import org.wmbus.protocol.config.WMBusMasterConfig;
import org.wmbus.protocol.infrastructure.ECCTable;
import org.wmbus.protocol.infrastructure.PathChooser;
import org.wmbus.protocol.messages.*;
import org.wmbus.simulation.WMBusSimulation;
import org.wmbus.simulation.convergence.model.ConvergenceModel;
import org.wmbus.simulation.convergence.state.ConvergenceState;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.*;


public class WMBusMaster extends WMbusDevice {

    private final SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> networkGraphECC;

    private ArrayList<Integer> path;

    public PrintWriter log;
    public PrintWriter band_log;

    private Request sending;
    private Integer  endNode;
    private ArrayDeque<Integer> pathEncoded;
    private DecimalFormat formatter = new DecimalFormat("#,###.00");
    private Integer fixedNode;
    public PrintWriter log_fault;

    public WMBusMaster(WMBusSimulation simulation, SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> graph) {
        super(simulation,0);
        this.networkGraphECC = graph;

    }



    private ArrayDeque<Integer> encodePath(List<Integer> path) {
        ArrayDeque<Integer> hopList = new ArrayDeque<Integer>();

        for (Integer node : path) {
            hopList.add(node);
        }
        return hopList;
    }

    public void printGraph(SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> networkGraphECC){
        for (DefaultWeightedEdge e: networkGraphECC.edgeSet()){
            Logger.trace("From: "+networkGraphECC.getEdgeSource(e)+" To: "+networkGraphECC.getEdgeTarget(e)+" With  weight: "+networkGraphECC.getEdgeWeight(e));
        }

    }

    public void lifeCycle()  {

        WMBusMaster m = this;
        Integer masterNode = (0);
        // Generate fixed list node.
        ArrayList<Integer> dest = getDestinations(this.simulation.getwMbusNetwork().getNodes().size()); // without master.

        Integer fixedNodeIndex = 0;
        long stabilityConvergenceTimes = 0;
        double average = 0;
        double sum = 0;
        double index = 0;

        /*
         * come descrivi una pecora brutta ?
		 * bela fuori ...e  bella dentro ...
         */
        Logger.trace("WMBusSimulation starts");
        PathChooser pathChooser = new PathChooser(this.simulation);
        ConvergenceModel convergenceModel = this.simulation.getwMbusSimulationConvergence();
        int convMeasure = ConvergenceState.CONVERGENCE_STATE_CONTINUE;

        while (convMeasure == ConvergenceState.CONVERGENCE_STATE_CONTINUE) {
            // System.out.println(this.simulation.getwMbusNetwork().getDistanceGraph().edgeSet());
            //fixedNode = randomGen.nextInt(graph.vertexSet().size() - 1) +1;
            if (fixedNodeIndex.equals(dest.size())) {
                fixedNodeIndex = 0;
                fixedNode = dest.get(fixedNodeIndex);
                fixedNodeIndex++;
            } else {
                fixedNode = dest.get(fixedNodeIndex);
                fixedNodeIndex++;
            }

            // this.printGraph(this.networkGraphECC);
            //TwoApproxMetricTSP salesman = new TwoApproxMetricTSP<Integer, DefaultWeightedEdge>();
            Logger.info("Search node for "+fixedNode);
            path = pathChooser.searchPath(this.networkGraphECC,fixedNode,this.simulation.getwMbusSimulationConfig().CONF_WAKEUP);
            //System.out.println(path);
            try {
                path.remove((0));
            } catch (Exception e) {
                //System.out.println("No master node "+fixedNode);
                Logger.error("Node master not found");
                throw new IllegalArgumentException();
                //continue;
                // the fuck?
            }
            //System.out.println(path);
            Logger.info("Path: "+path);
            Logger.info("Send message #"+this.simulation.getResults().masterSentMessage+ "");
            this.simulation.getResults().masterSumPath+=path.size();
            sending = new Request(this.simulation,0, path);
            double answer = 0;
            // Convergence stuff
            // long prevFault = this.simulation.getResults().masterTrasmissionFaultWithNoUpdate + this.simulation.getResults().masterTrasmissionFaultWithUpdate;
            //double prevPercFault = this.simulation.getResults().masterSentMessage==0?0:prevFault/this.simulation.getResults().masterSentMessage;
            // update master number of messages sent.
            this.simulation.getResults().masterSentMessage+=1;


            answer = this.transmit(sending); // Going down.

            if (answer == WMBusCommunicationState.TIMEOUT){
                this.simulation.getResults().masterTrasmissionFaultWithNoUpdate++;
                this.triggerTimeout();
            }
            // Convergence stuff
            double fault = (
                    (this.simulation.getResults().masterTrasmissionFaultWithNoUpdate + this.simulation.getResults().masterTrasmissionFaultWithUpdate)/this.simulation.getResults().masterSentMessage);


            convMeasure = convergenceModel.addMeasure(fault);
            // Call Convergence event.
            this.simulation.getWMbusEvents().provideMeasure(fault,convMeasure,convergenceModel.percentageConvergence);

            if (convMeasure == ConvergenceState.CONVERGENCE_STATE_STOP_CONVERGENCE){
                this.simulation.getResults().globalConvergence = true;
            } else if (convMeasure == ConvergenceState.CONVERGENCE_STATE_STOP_NOTCONVERGENCE){
                this.simulation.getResults().globalConvergence = false;
            }

         }

    }

    /**
     * Update internal graph structure.
     * @param destination
     */
    @Override
    public void updateECCStructures(int destination){
        super.updateECCStructures(destination);
        DefaultWeightedEdge edge = this.simulation.getwMbusNetwork().getEccGraph().getEdge((0),(destination));
        DefaultWeightedEdge edge2 = this.simulation.getwMbusNetwork().getEccGraph().getEdge((destination),(0));
        //System.out.println("Update link state from to "+destination+" with error maximum");
        this.networkGraphECC.setEdgeWeight(edge, WMBusCommunicationState.TIMEOUT);
        this.networkGraphECC.setEdgeWeight(edge2, WMBusCommunicationState.TIMEOUT);
    }

    /**
     * Receive a packet
     * @param message
     */
    @Override
    public void receive(WMbusMessage message)  {
        super.receive(message);
        double tras_result = 0; // fixed value.
        // WMBusStats.hopCount += 1;

        // Check message type.
        if (message.getMessageType()== WMBusPacketType.PACKET_REQUEST) {
            // no need to do anything.
        } else
        if (message.getMessageType()== WMBusPacketType.PACKET_RESPONSE) {

            Response res = (Response) message;
            Logger.trace("Data: "+((Response) message).getData());
            // No data means error.
            if (res.getData()==0){
                this.simulation.getResults().masterTrasmissionFaultWithUpdate++;
            }else{
                this.simulation.getResults().masterTrasmissionSuccess++;
            }
            for(ECCTable entry : res.getECCTables()){
                for (Integer destination: entry.getEntries().keySet()){
                    DefaultWeightedEdge edge = this.simulation.getwMbusNetwork().getEccGraph().getEdge((entry.getNode()),(destination));
                    Logger.trace("Update link state from "+entry.getNode()+" to "+destination+" with error "+entry.getEntries().get(destination));
                    this.simulation.getResults().masterUpdateLink++;
                    this.networkGraphECC.setEdgeWeight(edge,entry.getEntries().get(destination));
                }
            }
        }
    }
    private ArrayList<Integer> getDestinations(int maxDest) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        // 1 without master.
        for (int i = 1; i < maxDest;i++){
            list.add(i);
        }
        if (WMBusMasterConfig.CONF_DESTINATION_FETCH == WMBusMasterConfig.DESTINATION_FETCH_RANDOM){
            Collections.shuffle(list);
        }
        return list;
    }
}
