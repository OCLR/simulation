package simul.protocol;

import simul.infrastructure.MbusMessage;

import java.util.ArrayDeque;



/**
 * Created by Federico Falconi on 05/07/2017.
 */

public class Response extends MbusMessage {
    private int code, token, payloadLen;
    private int nextHop;
    private ArrayDeque<NoiseTable> noiseTables;

    public int getSize() {
        int size = super.getSize();
        size += (4*Integer.SIZE);
        size += (
                 (noiseTables.size()*Integer.SIZE)+ //Index
                 (noiseTables.size()*Integer.SIZE) //Nodes
                );
        for (NoiseTable ns: noiseTables){
            size += (ns.getEntries().size()*(Integer.SIZE+Double.SIZE));
        }
        size += (payloadLen*8);// Payload size(in bytes).
        
        return size;
    }
    
    public Response(int code, int token, int payloadLen, int source, int nextHop, ArrayDeque<NoiseTable> noiseTables) {
        super(payloadLen + 4,source);
        this.code = code;
        this.token = token;
        this.payloadLen = payloadLen;
        this.nextHop = nextHop;
        this.noiseTables = noiseTables;
    }


    public Response(Response other) {
        super(other.getPayloadLen(),other.getSource());
        this.code = other.code;
        this.token = other.token;
        this.payloadLen = other.payloadLen;
        this.nextHop = other.nextHop;
        this.noiseTables = new ArrayDeque<NoiseTable>(other.noiseTables);
    }


    public int getCode() {
        return code;
    }


    public int getToken() { return token; }


    public int getPayloadLen() {
        return payloadLen;
    }


    public int getNextHop() {
        return  nextHop;
    }


  
    public ArrayDeque<NoiseTable> getNoiseTables() { return noiseTables; }

    public void setNextHop( int nextHop) {
        this.nextHop = nextHop;
    }
}