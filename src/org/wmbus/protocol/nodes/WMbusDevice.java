package org.wmbus.protocol.nodes;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.wmbus.protocol.infrastructure.ECCTable;
import org.wmbus.protocol.messages.WMBusCommunicationState;
import org.wmbus.protocol.messages.WMBusPacketType;
import org.wmbus.protocol.messages.WMbusMessage;
import org.wmbus.protocol.simulation.WMBusSimulation;
import yang.simulation.network.MasterGraphNode;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Set;


public abstract class WMbusDevice {


    protected final WMBusSimulation simulation;
    private int nodeID;
    private HashMap<Integer, Double> eccTable = new HashMap<Integer, Double>();
    private HashMap<Integer, Boolean> updatedEccTable = new HashMap<Integer, Boolean>();
    public long timeoutPacket = 0;
    public long receivedPacket = 0;
    public long sentPacket = 0;
    public long sentPacketBroadcast = 0;
    public long receivePacketBroadcastTimeout = 0;
    public long receivePacketBroadcastNotForMe = 0;


    public WMbusDevice(WMBusSimulation simulation, int nodeID) {
        /**
         * M-bus network
         */
        this.simulation = simulation;
        this.nodeID = nodeID;
    }

    /**
     * Trasmit an Mbus Message and change the network variation
     *
     * @param message Mbus message
     */
    public  double transmit(WMbusMessage message)   {
        boolean hit;
        double nodeRes,res;
        int nodeTarget = -1;
        Set<DefaultWeightedEdge> outgoingEdges = this.simulation.getwMbusNetwork().getOutgoingEdges(this.nodeID); // get all neighbors.
        message.setSource(this.nodeID);
        //System.out.println("TRY");
        double packetSize = message.getMessageSize();
        this.sentPacket++;
        // Every node receive a message.

        this.simulation.getResults().deviceTrasmissionCommunication++;

        if (message.getMessageType()== WMBusPacketType.PACKET_REQUEST){
            this.simulation.getResults().globalTrasmissionRequestSum+=message.getMessageSize();
            this.simulation.getResults().globalTrasmissionRequestPayloadNoParityBitSum+=message.getMessageSizeOnlyPayloadWithoutParitybit();
            this.simulation.getResults().globalTrasmissionRequestPayloadParityBitSum +=message.getMessageSizeOnlyPayloadWithParitybit();
            this.simulation.getResults().globalTrasmissionRequestHeaderSum +=message.getMessageHeader();
            this.simulation.getResults().globalTrasmissionRequestBlockNumber += message.getMessageBlockCount();
            this.simulation.getResults().globalRequestTrasmissionCommunication +=1;
        }else{
            this.simulation.getResults().globalTrasmissionResponseSum+=message.getMessageSize();
            this.simulation.getResults().globalTrasmissionResponsePayloadNoParityBitSum+=message.getMessageSizeOnlyPayloadWithoutParitybit();
            this.simulation.getResults().globalTrasmissionResponsePayloadParityBitSum +=message.getMessageSizeOnlyPayloadWithParitybit();
            this.simulation.getResults().globalTrasmissionResponseHeaderSum +=message.getMessageHeader();
            this.simulation.getResults().globalTrasmissionResponseBlockNumber += message.getMessageBlockCount();
            this.simulation.getResults().globalResponseTrasmissionCommunication +=1;
        }


        int attemptNumber = this.simulation.getwMbusConfig().CONF_NUMBER_OF_RETRASMISSION+1;
        // try attempt number of attempt.
        res = -1;
        //Logger.info("TX BROADCAST "+message.toString());
        //Logger.info("Sending packet from "+message.getSource()+" to "+message.getDestination()+":: ");
        while (attemptNumber > 0 &&   !WMBusCommunicationState.isOK(res)){
            res = -1;

            for (DefaultWeightedEdge edge : outgoingEdges) {// send to all neighbors.
                this.sentPacketBroadcast++;
                MasterGraphNode destinationNode = new MasterGraphNode(message.getDestination());
                WMbusDevice destinationMbusNode = this.simulation.getwMbusNetwork().getNode(destinationNode.getStaticAddress());
                nodeRes = destinationMbusNode.receiveAck(message); // son.getHopDestination()
                if (WMBusCommunicationState.isOK(nodeRes)){
                    res = nodeRes;
                    nodeTarget = destinationNode.getStaticAddress();
                }
            }
            attemptNumber--;
            if (!WMBusCommunicationState.isOK(res)){
                this.simulation.getResults().deviceRetrasmissionCommunication++;
                //System.out.print(" RETR, ");
            }
        }
        // means error.
        if (!WMBusCommunicationState.isOK(res)) {
            // Update source to target.
            //System.out.println(" FAULT ");
            this.updateECCStructures(message.getDestination());
            this.simulation.getResults().deviceTrasmissionTimeoutCommunication++;
            return WMBusCommunicationState.TIMEOUT;
        }
        // LO(" OK  ");
        // After receive packet
        this.simulation.getResults().deviceSuccessTrasmissionCommunication++;
        this.simulation.getwMbusNetwork().getNode(nodeTarget).receive(message);

        return 0;// The sender doesn't have the nodeRes.

    }

    public void updateECCStructures(int destination){
        this.ECCUpdateLink(destination, WMBusCommunicationState.TIMEOUT);
    }

    public double receiveAck(WMbusMessage message){
        // TODO get the link.
        double ecc = message.computeECC(this.simulation.getwMbusNetwork().getBer(message.getSource(),message.getDestination()));

        if (ecc==2){
            //((Logger.info("RX BROADCAST TIMEOUT "+message.toString());
            this.receivePacketBroadcastTimeout++;
            return WMBusCommunicationState.TIMEOUT;
        }else {
            if (this.nodeID != message.getDestination()) {
                //Logger.info("RX BROADCAST NOTFORME "+message.toString());
                this.receivePacketBroadcastNotForMe++;
                return (WMBusCommunicationState.NOT_FOR_ME);
            }
        }
        // Update internal link.
        //  message.getSource() ->  this.nodeID
        this.ECCUpdateLink(message.getSource(),ecc);
        //Logger.info("RX BROADCAST OK "+ecc);
        return ecc;
    }

    private void ECCUpdateLink(int destination, double ecc) {
        if (this.eccTable.containsKey(destination)){
            this.updatedEccTable.put(destination,(this.eccTable.get(destination)!=ecc));
        }else{
            this.updatedEccTable.put(destination,true);
        }
        this.eccTable.put(destination,ecc);

    }

    public void receive(WMbusMessage message){
        this.receivedPacket++;
    }

    /**
     * Get node id
     * @return
     */
    public int getNodeID() {
        return nodeID;
    }

    /**
     * attachECCTable
     */
    public ArrayDeque<ECCTable> attachECCTable(ArrayDeque<ECCTable> list){
        // Add only if a node updates.
        if (this.updatedEccTable.containsValue(true)){
            ECCTable nodeECCTable = new ECCTable(this.nodeID);// Create it's own ecctable.
            for (Integer nodeEcc: this.updatedEccTable.keySet()){
                boolean v = this.updatedEccTable.get(nodeEcc);
                if (v){
                    // Add it's own value of ecc.
                    nodeECCTable.getEntries().put(nodeEcc,this.eccTable.get(nodeEcc));
                }
            }
            if (!nodeECCTable.getEntries().isEmpty()){
                list.add(nodeECCTable);
            }
        }
        // return the list as it is.
        return list;

    }
    public void triggerTimeout(){
        this.timeoutPacket++;
    }
}
