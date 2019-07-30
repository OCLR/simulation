package org.wmbus.protocol.messages;


import org.wmbus.simulation.WMBusSimulation;

import java.util.ArrayList;


public class Request extends WMbusMessage {
    private byte type = WMBusPacketType.PACKET_REQUEST;

    public ArrayList<Integer> getHops() {
        return hops;
    }

    private ArrayList<Integer> hops; /* a list of nodes. 4 byte for each node.*/

    public Request(WMBusSimulation simulation, int source, ArrayList<Integer> hops){
        super(simulation,source);
        this.hops = hops;
    }


    @Override
    public int getDestination() {
        if (this.hops.size()==0){ // 1 element or nothing.
            throw new IllegalArgumentException();
        }else{
            return (int) this.hops.get(0);
        }
    }
    @Override
    public int getMessageSize() {
        return this.computeFullFrameSize((this.hops.size()*4)+1);
    }

    @Override
    public int getMessageSizeOnlyPayloadWithParitybit() {
        return this.computeFullFrameSizePayloadWithParityBit((this.hops.size()*4)+1);
    }

    @Override
    public int getMessageSizeOnlyPayloadWithoutParitybit() {
        return this.computeFullFrameSizePayloadWithoutParityBit((this.hops.size()*4)+1);
    }



    @Override
    public int getMessageBlockCount() {
        return this.computeFullFrameCount((this.hops.size()*4)+1);
    }

    public int getFinalDestination() {
        if (this.hops.size()==0){
            throw new IllegalArgumentException();
        }else{
            return this.hops.get(this.hops.size()-1);
        }
    }


    public int getMessageType(){
        return WMBusPacketType.PACKET_REQUEST;
    }

    public String toString(){
        ArrayList<Integer> h = new ArrayList<Integer>(this.hops);
        String hopsStr ="";
        for (int i = 0; i < hops.size(); i++) {
            if (i+1 < hops.size()){
                hopsStr += h.get(i)+",";
            }else{
                hopsStr += h.get(i);
            }

        }
        return "TYPE:"+this.type+","+super.toString()+",HOPS:"+hopsStr;
    }
}
