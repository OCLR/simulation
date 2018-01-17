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
    private int destination;
    private ArrayDeque<Integer> hopList;
	private int hopDestination;

    public int getHopDestination() {
		return hopDestination;
	}


	public void setHopDestination(int hopDestination) {
		this.hopDestination = hopDestination;
	}


	public Request(int code, int token, int payloadLen, int source,int hopDestination,int destination, ArrayDeque<Integer> hopList) {
        super(hopList.size() + payloadLen + 4);
        this.code = code;
        this.token = token;
        this.payloadLen = payloadLen;
        this.source = source;
        this.destination = destination;
        this.hopDestination = hopDestination;
        this.hopList = new ArrayDeque<Integer>(hopList);
    }


    public Request(Request other) {
        super(other);
        this.code = other.code;
        this.token = other.token;
        this.payloadLen = other.payloadLen;
        this.source = other.source;
        this.hopDestination = other.hopDestination;
        this.destination = other.destination;
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


	public int getDestination() {
		return destination;
	}


	public void setDestination(int destination) {
		this.destination = destination;
	}
}
