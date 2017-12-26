package simul.protocol;

import simul.infrastructure.MbusMessage;

import java.util.ArrayDeque;



/**
 * Created by Federico Falconi on 05/07/2017.
 */

public class Response extends MbusMessage {
    private int code, token, payloadLen;
    private int source;
    private int nextHop;
    private ArrayDeque<NoiseTable> noiseTables;

    public Response(int code, int token, int payloadLen, int source, int nextHop, ArrayDeque<NoiseTable> noiseTables) {
        super(payloadLen + 4);
        this.code = code;
        this.token = token;
        this.payloadLen = payloadLen;
        this.source = source;
        this.nextHop = nextHop;
        this.noiseTables = noiseTables;
    }


    public Response(Response other) {
        super(other);
        this.code = other.code;
        this.token = other.token;
        this.payloadLen = other.payloadLen;
        this.source = other.source;
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


    public int getSource() {
        return source;
    }


    public ArrayDeque<NoiseTable> getNoiseTables() { return noiseTables; }
}