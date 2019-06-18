package simul.messages;

import com.sun.jdi.InternalException;
import com.sun.jdi.request.InvalidRequestStateException;
import simul.protocol.SimulationConfiguration;

import java.util.ArrayDeque;
import java.util.ArrayList;


public class Request extends MbusMessage {
    private byte type = SimulationConfiguration.PACKET_REQUEST;

    public ArrayDeque<Integer> getHops() {
        return hops;
    }

    private ArrayDeque<Integer> hops; /* a list of nodes. 4 byte for each node.*/

    public Request(int source, ArrayDeque<Integer> hops){
        super(source);
        this.hops = hops;
    }


    @Override
    public int getDestination() {
        if (this.hops.size()==0){ // 1 element or nothing.
            throw new InvalidRequestStateException();
        }else{
            return (int) this.hops.getFirst();
        }
    }

    @Override
    public int getMessageSize() {
        return this.computeFullFrameSize((this.hops.size()*4)+1);
    }

    @Override
    public int getMessageBlockCount() {
        return this.computeFullFrameCount((this.hops.size()*4)+1);
    }

    public int getFinalDestination() {
        if (this.hops.size()==0){
            throw new InvalidRequestStateException();
        }else{
            return this.hops.getLast();
        }
    }


    public int getMessageType(){
        return SimulationConfiguration.PACKET_REQUEST;
    }
}
