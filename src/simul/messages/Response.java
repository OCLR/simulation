package simul.messages;

import simul.infrastructure.ECCTable;
import simul.protocol.SimulationConfiguration;

import java.util.ArrayDeque;


public class Response extends MbusMessage {


    private ArrayDeque<ECCTable> ECCTables; /* a list of nodes. 4 byte for each node.*/
    private byte type = SimulationConfiguration.PACKET_RESPONSE;
    private int destination = 0;


    private float data= 0; /* 4 byte*/

    public Response(int source,int destination, ArrayDeque<ECCTable> ECCTables){
        super(source);
        this.ECCTables = ECCTables;
        this.destination = destination;
    }

    public float getData() {
        return data;
    }

    public void setData(float data) {
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
}