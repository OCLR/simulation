package simul.protocol;

import simul.infrastructure.MbusMessage;

import java.util.ArrayDeque;
import java.util.ArrayList;
import simul.cache.base.cachePacketTuple;

/**
 * Created by Federico Falconi on 05/07/2017.
 */
public class Request extends MbusMessage {

    public final static int VERSION = 1;
    private int code, token, payloadLen;
    private int destination;
    private ArrayDeque<Integer> hopList;
    private ArrayList<cachePacketTuple> cacheList;
    private boolean partialCached;

    public boolean isPartialCached() {
        return partialCached;
    }

    public void setPartialCached(boolean partialCached) {
        this.partialCached = partialCached;
    }
    public ArrayList<cachePacketTuple> getCacheList() {
        return cacheList;
    }

    public void setCacheList(ArrayList<cachePacketTuple> cacheList) {
        this.cacheList = cacheList;
    }
    private int hopDestination;
    
    public int getSize() {
        int size = super.getSize();
        int pathSize = 0;
        /*if (!this.isPartialCached()){
            pathSize = this.hopList.size();
        }else{
            pathSize = this.getCacheList().size();
        }*/
        pathSize = this.hopList.size();
        size += (5*Integer.SIZE);
        
        size += (pathSize*Integer.SIZE);
        size += (payloadLen*8);// Payload size(in bytes).
        return size;
    }
    
    public int getHopDestination() {
        return hopDestination;
    }

    public void setHopDestination(int hopDestination) {
        this.hopDestination = hopDestination;
    }
    private static int getPathSize(Object hopList){
        int size = 0;
        if (hopList instanceof ArrayDeque){
            size = ((ArrayDeque) hopList).size() ;
        }else if (hopList instanceof ArrayList){
            size = ((ArrayList) hopList).size()*2 ;
        }
        return size;
    }
    public Request(int code, int token, int payloadLen, int source, int hopDestination, int destination, Object hopList) {
        super(Request.getPathSize(hopList)+ payloadLen + 4, source);
        this.code = code;
        this.token = token;
        this.payloadLen = payloadLen;
        this.destination = destination;
        this.hopDestination = hopDestination;
        this.partialCached = (hopList instanceof ArrayList);
        if ((hopList instanceof ArrayList)){
            this.cacheList = new ArrayList<cachePacketTuple>((ArrayList) hopList);
        }else
        if ((hopList instanceof ArrayDeque)){
            this.hopList = new ArrayDeque<Integer>((ArrayDeque) hopList);
        }
        
        
    }

    public Request(Request other) {
        super(other.getPayloadLen(),other.getSource());
        this.code = other.code;
        this.token = other.token;
        this.payloadLen = other.payloadLen;
        this.hopDestination = other.hopDestination;
        this.destination = other.destination;
        if (!other.isPartialCached()){
            this.hopList = new ArrayDeque<Integer>(other.getHopList());
        }else{
            this.cacheList = new ArrayList<cachePacketTuple>(other.cacheList);
        }
        this.partialCached = other.isPartialCached();
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

    public ArrayDeque<Integer> getHopList() {
        return hopList;
    }
    public void setHopList(ArrayDeque<Integer> hops) {
        this.hopList = hops;
    }

    public int getDestination() {
        return destination;
    }

    public void setDestination(int destination) {
        this.destination = destination;
    }

    public int contains(int slaveAddress) {
        for (int i = 0; i < this.cacheList.size(); i++) {
            cachePacketTuple c = this.cacheList.get(i);
            if (c.getSource() == slaveAddress){
                return c.getNexthop();
            }
        }
        return -1;
    }
    public void remove(int slaveAddress) {
        for (int i = 0; i < this.cacheList.size(); i++) {
            cachePacketTuple c = this.cacheList.get(i);
            if (c.getSource() == slaveAddress){
                this.cacheList.remove(c);
                break;
            }
        }
        
    }

    

   
}
