package simul.protocol;

import simul.infrastructure.MbusMessage;

import java.util.ArrayDeque;

/**
 * Created by Federico Falconi on 05/07/2017.
 */
public class Request extends MbusMessage {
    public final static int VERSION = 1;
    private int code, token, payloadLen;
    private int source;
    private ArrayDeque<Integer> hopList;

    public Request(int code, int token, int payloadLen, int source, ArrayDeque<Integer> hopList) {
        super(hopList.size() + payloadLen + 4);
        this.code = code;
        this.token = token;
        this.payloadLen = payloadLen;
        this.source = source;
        this.hopList = new ArrayDeque<Integer>(hopList);
    }


    public Request(Request other) {
        super(other);
        this.code = other.code;
        this.token = other.token;
        this.payloadLen = other.payloadLen;
        this.source = other.source;
        this.hopList = new ArrayDeque<Integer>(other.hopList);
    }


    public int getCode() {
        return code;
    }


    public int getToken() {
        return token;
    }


    public int getPayloadLen() {
        return payloadLen;
    }


    public int getSource() {
        return source;
    }


    public ArrayDeque<Integer> getHopList() {
        return hopList;
    }
}
