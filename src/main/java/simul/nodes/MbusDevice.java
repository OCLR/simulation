package simul.nodes;

import org.jgrapht.graph.DefaultWeightedEdge;

import org.pmw.tinylog.Logger;
import simul.infrastructure.ECCTable;
import simul.infrastructure.MbusNetwork;
import simul.messages.CommunicationState;
import simul.messages.MbusMessage;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Set;

import simul.protocol.*;

/**
 * Created by Federico Falconi on 04/07/2017.
 */
public abstract class MbusDevice {



    private int nodeID;
    public MbusNetwork network;



    private HashMap<Integer, Double> eccTable = new HashMap<Integer, Double>();
    private HashMap<Integer, Boolean> updatedEccTable = new HashMap<Integer, Boolean>();

    public long timeoutPacket = 0;
    public long receivedPacket = 0;
    public long sentPacket = 0;
    public long sentPacketBroadcast = 0;
    public long receivePacketBroadcastTimeout = 0;
    public long receivePacketBroadcastNotForMe = 0;


    public MbusDevice(MbusNetwork owner, int nodeID) {
        /**
         * M-bus network
         */
        this.network = owner;
        this.nodeID = nodeID;
    }

    /**
     * Trasmit an Mbus Message and change the network variation
     *
     * @param message Mbus message
     * @throws simul.protocol.CommunicationFault
     */
    public  double transmit(MbusMessage message)   {
        boolean hit;
        double nodeRes,res;
        int nodeTarget = -1;
        Set<DefaultWeightedEdge> outgoingEdges = this.network.getOutgoingEdges(this.nodeID); // get all neighbors.
        message.setSource(this.nodeID);
        //System.out.println("TRY");
        float packetSize = message.getMessageSize();
        this.sentPacket++;
        // Every node receive a message.

        if (this.network.getStats().)
        this.network.getStats().globalTrasmissionCommunication++;
        this.network.getStats().globalTrasmissionPacketSum+=message.getMessageSize();
        this.network.getStats().globalTrasmissionPayloadNoParityBitSum+=message.getMessageSizeOnlyPayloadWithoutParitybit();
        this.network.getStats().globalTrasmissionPayloadParityBitSum +=message.getMessageSizeOnlyPayloadWithParitybit();
        this.network.getStats().globalTrasmissionHeaderSum +=message.getMessageHeader();
        this.network.getStats().globalTrasmissionBlockNumber += message.getMessageBlockCount();

        int attemptNumber = SimulationConfiguration.CONF_NUMBER_OF_RETRASMISSION+1;
        // try attempt number of attempt.
        res = -1;
        Logger.info("TX BROADCAST "+message.toString());
        //Logger.info("Sending packet from "+message.getSource()+" to "+message.getDestination()+":: ");
        while (attemptNumber > 0 &&   !CommunicationState.isOK(res)){
            res = -1;

            for (DefaultWeightedEdge edge : outgoingEdges) {// send to all neighbors.
                this.sentPacketBroadcast++;
                MasterGraphNode  destinationNode = new MasterGraphNode(message.getDestination());
                MbusDevice destinationMbusNode = this.network.getNode(destinationNode.getStaticAddress());
                nodeRes = destinationMbusNode.receiveAck(message); // son.getHopDestination()
                if (CommunicationState.isOK(nodeRes)){
                    res = nodeRes;
                    nodeTarget = destinationNode.getStaticAddress();
                }
            }
            attemptNumber--;
            if (!CommunicationState.isOK(res)){
                this.network.getStats().globalRetrasmissionCommunication++;
                //System.out.print(" RETR, ");
            }
        }
        // means error.
        if (!CommunicationState.isOK(res)) {
            // Update source to target.
            //System.out.println(" FAULT ");
            this.updateECCStructures(message.getDestination());
            return CommunicationState.TIMEOUT;
        }
        // LO(" OK  ");
        // After receive packet
        this.network.getNode(nodeTarget).receive(message);

        return 0;// The sender doesn't have the nodeRes.

    }

    public void updateECCStructures(int destination){
        this.ECCUpdateLink(destination,CommunicationState.TIMEOUT);
    }

    public double receiveAck(MbusMessage message){
        double ecc = message.computeECC(this.network.getBer());

        if (ecc==2){
            Logger.info("RX BROADCAST TIMEOUT "+message.toString());
            this.receivePacketBroadcastTimeout++;
            return CommunicationState.TIMEOUT;
        }else {
            if (this.nodeID != message.getDestination()) {
                Logger.info("RX BROADCAST NOTFORME "+message.toString());
                this.receivePacketBroadcastNotForMe++;
                return (CommunicationState.NOT_FOR_ME);
            }
        }
        // Update internal link.
        //  message.getSource() ->  this.nodeID
        this.ECCUpdateLink(message.getSource(),ecc);
        Logger.info("RX BROADCAST OK "+ecc);
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

    public void receive(MbusMessage message){
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
