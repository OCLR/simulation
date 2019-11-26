package org.wmbus.protocol.messages;


import org.wmbus.protocol.infrastructure.ECCTable;
import org.wmbus.simulation.WMBusSimulation;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class Response extends WMbusMessage {


    private ArrayDeque<ECCTable> ECCTables; /* a list of nodes. 4 byte for each node.*/
    private byte type = WMBusPacketType.PACKET_RESPONSE;
    private int destination = 0;


    private double data= 0; /* 4 byte*/

    public Response(WMBusSimulation simulation, int source, int destination, ArrayDeque<ECCTable> ECCTables){
        super(simulation,source);
        this.ECCTables = ECCTables;
        this.destination = destination;
    }

    public double getData() {
        return data;
    }

    public void setData(double data) {
        this.data = data;
    }



    @Override
    public int getDestination() {
        return this.destination;
    }
    @Override
    public int getMessageSize() {
        int byteSize = 0;
        /* Determine the bytesize*/
        for (ECCTable n: this.ECCTables){
            byteSize+=n.getBytesSize();
        }
        return this.computeFullFrameSize(byteSize+1+1); // destination+type
    }
    @Override
    public int getMessageSizeOnlyPayloadWithoutParitybit() {
        int byteSize = 0;
        /* Determine the bytesize*/
        for (ECCTable n: this.ECCTables){
            byteSize+=n.getBytesSize();
        }
        return this.computeFullFrameSizePayloadWithoutParityBit(byteSize+1+1); // destination+type
    }

    @Override
    public int getMessageSizeOnlyPayloadWithParitybit() {
        int byteSize = 0;
        /* Determine the bytesize*/
        for (ECCTable n: this.ECCTables){
            byteSize+=n.getBytesSize();
        }
        return this.computeFullFrameSizePayloadWithParityBit(byteSize+1+1); // destination+type
    }

    @Override
    public int getMessageBlockCount() {
        int byteSize = 0;
        /* Determine the bytesize*/
        for (ECCTable n: this.ECCTables){
            byteSize+=n.getBytesSize();
        }
        return this.computeFullFrameCount(byteSize+1+1); // destination+type
    }

    public int getMessageType(){
        return WMBusPacketType.PACKET_RESPONSE;
    }
    public ArrayDeque<ECCTable> getECCTables() {
        return ECCTables;
    }
    public String toString(){
        ArrayList<ECCTable> ecc = new ArrayList<ECCTable>( this.ECCTables);
        String res = "ECCTABLES";
        for (int i = 0; i < this.ECCTables.size(); i++) {
            res +="(";
            res += ecc.get(i).toString();
            res+=")" ;
        }
        return "TYPE:"+this.type+","+super.toString()+",DESTINATION:"+destination+" "+res;
    }
}