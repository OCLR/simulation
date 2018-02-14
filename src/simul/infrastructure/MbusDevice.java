package simul.infrastructure;

import co.paralleluniverse.fibers.SuspendExecution;
import desmoj.core.simulator.*;
import simul.protocol.Response;
import simul.protocol.Request;

import java.util.HashMap;

/**
 * Created by Federico Falconi on 04/07/2017.
 */

public abstract class MbusDevice extends SimProcess {
    private int position;
    protected MbusNetwork network;
    protected MbusMessage lastReceived;

    
    public MbusDevice(Model owner, String name, Boolean showInTrace, int pos) {
        super(owner, name, showInTrace);
        /** M-bus network */
        network = (MbusNetwork)owner;
        
        position = pos;
    }

    /**
     * Trasmit an Mbus Message and change the network variation
     * 
     * @param message Mbus message
     * @param variation True of false for noise variation.
     * @throws SuspendExecution
     */
    protected void transmit(MbusMessage message, boolean variation) throws SuspendExecution{
        HashMap<Integer, Double> outgoingEdges = network.getOutgoingEdges(position); // get all neighbors.
        MbusMessage son;
        double error;

        if (variation) {
            network.updateNoise();
        }

        hold(new TimeSpan(message.getLength())); // simulation message send delay.

        //if (TimeInstant.isAfter(presentTime(), new TimeInstant(network.getLasting()/2))) {
        	// package sended.
        network.headerSum += message.getLength() - 20;
        network.messagesSent++; // increase package sended.
        //}

        for (Integer key : outgoingEdges.keySet()) {// send to all neighbors.
        	// if it is a reply with data.
        	if (message.getClass() == Response.class) { // consider the message as a response
                son = new Response((Response)message);
            }
            else { // consider a message as a request otherwise.
                son = new Request((Request)message); 
            }
            error = outgoingEdges.get(key);
            // Error can happen and the message compute it.
            son.generateErrors(error);
            // Every node receive a message.
            network.getNode(key).receive(son);
            network.getNode(key).activate();
        }
        
    }


    public void receive(MbusMessage message) {
        lastReceived = message;
    }


    protected MbusMessage retrieveMsg() throws SuspendExecution {
        MbusMessage msg = lastReceived;
        lastReceived = null;
        return msg;
    }


    protected MbusMessage getReceived() {
        return lastReceived;
    }
}
