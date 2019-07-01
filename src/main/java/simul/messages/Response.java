package simul.messages;

import simul.infrastructure.ECCTable;
import simul.protocol.SimulationConfiguration;

import java.util.ArrayDeque;
import java.util.ArrayList;


public class Response extends MbusMessage {


    private ArrayDeque<ECCTable> ECCTables; /* a list of nodes. 4 byte for each node.*/
    private byte type = SimulationConfiguration.PACKET_RESPONSE;
    private int destination = 0;


    private double data= 0; /* 4 byte*/

    public Response(int source,int destination, ArrayDeque<ECCTable> ECCTables){
        super(source);
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
        return this.computeFullFrameSize(byteSize+1+4); // destination+type
    }
    @Override
    public int getMessageSizeOnlyPayloadWithoutParitybit() {
        int byteSize = 0;
        /* Determine the bytesize*/
        for (ECCTable n: this.ECCTables){
            byteSize+=n.getBytesSize();
        }
        return this.computeFullFrameSizePayloadWithoutParityBit(byteSize+1+4); // destination+type
    }

    @Override
    public int getMessageSizeOnlyPayloadWithParitybit() {
        int byteSize = 0;
        /* Determine the bytesize*/
        for (ECCTable n: this.ECCTables){
            byteSize+=n.getBytesSize();
        }
        return this.computeFullFrameSizePayloadWithParityBit(byteSize+1+4); // destination+type
    }

    @Override
    public int getMessageBlockCount() {
        int byteSize = 0;
        /* Determine the bytesize*/
        for (ECCTable n: this.ECCTables){
            byteSize+=n.getBytesSize();
        }
        return this.computeFullFrameCount(byteSize+1+4); // destination+type
    }

    public int getMessageType(){
        return SimulationConfiguration.PACKET_RESPONSE;
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