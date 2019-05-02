package simul.infrastructure;

import co.paralleluniverse.fibers.SuspendExecution;
import desmoj.core.simulator.*;
import simul.protocol.Response;
import simul.protocol.Request;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import simul.protocol.CommunicationFault;

/**
 * Created by Federico Falconi on 04/07/2017.
 */
public abstract class MbusDevice {

    private int nodeID;
    public MbusNetwork network;
    protected MbusMessage lastReceived;
    protected long numberOfUpdates;
    protected long receivedPacket;
    protected long sentPacket;
    protected long faultPacket;
    protected long updateLocalNoiseTable;
    protected long sentPacketBroadcast;


    public MbusDevice(MbusNetwork owner, String name, Boolean showInTrace, int nodeID) {
        /**
         * M-bus network
         */
        this.network = owner;

        this.nodeID = nodeID;
        this.numberOfUpdates = 0;
        this.receivedPacket = 0;
        this.sentPacket = 0;
        this.updateLocalNoiseTable = 0;
        this.faultPacket = 0;
    }

    /**
     * Trasmit an Mbus Message and change the network variation
     *
     * @param message Mbus message
     * @param variation True of false for noise variation.
     * @throws simul.protocol.CommunicationFault
     * @throws SuspendExecution
     */
    public synchronized Response transmit(MbusMessage message, boolean variation) throws CommunicationFault  {
        boolean hit;
        HashMap<Integer, Double> outgoingEdges = network.getOutgoingEdges(this.nodeID); // get all neighbors.
        
        //System.out.println("TRY");

        // Every node receive a message.
        if (message.getClass() == Request.class) { // consider the message as a response
            Request son = new Request((Request) message);
            Response  nodeRes = null;
            Response  res = null;
            this.sentPacket++;
           for (Integer key : outgoingEdges.keySet()) {// send to all neighbors.
                this.sentPacketBroadcast++;

                son.generateErrors(network.configManager.updateSingleRequest(son));
                nodeRes = network.getNode(key).receive(son); // son.getHopDestination()
                if (nodeRes!=null){
                    res = nodeRes;
                }
            }
            
            if (res == null){
                throw new InternalError ("Send packet to no one....");
            }
            
            // setting source of resource. ( coming from me )
            res.setSource(son.getHopDestination());
            // setting destination for father.
            res.setNextHop(son.getSource());
            // Throw back a fault if something happens.
            double error = network.configManager.updateSingleResponse(res);
            // Update the node t
            
            if (error==2){
                this.faultPacket++;
                CommunicationFault  unRecoverable = new CommunicationFault(this.nodeID);
                unRecoverable.setUnRecoverable(true);
                throw unRecoverable;
            }
            return res;
        }else{
            return null;/* else { // consider a message as a request otherwise.
            Request son = new Request((Request) message);
            try{
                son.generateErrors();
            }
            catch(NullPointerException e){
                throw e;
            }
            return network.getNode(son.getHopDestination()).receive(son);
        }*/
        }
    }

    public synchronized Response receive(MbusMessage message) throws CommunicationFault{
        lastReceived = message;
        this.receivedPacket++;
        decode(message);
        return null;
    }

    protected MbusMessage retrieveMsg() throws SuspendExecution {
        MbusMessage msg = lastReceived;
        lastReceived = null;
        return msg;
    }

    protected MbusMessage getReceived() {
        return lastReceived;
    }

    public boolean decode(MbusMessage message)  {
        //throw new CommunicationFault(0); //To change body of generated methods, choose Tools | Templates.
        return false;
    }
}
