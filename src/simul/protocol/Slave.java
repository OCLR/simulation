package simul.protocol;

import co.paralleluniverse.fibers.SuspendExecution;
import desmoj.core.simulator.Model;

import desmoj.core.simulator.TimeSpan;
//import simul.cache.slaveCache;
//import simul.cache.base.cachePair;
import simul.infrastructure.MbusDevice;
import simul.infrastructure.MbusMessage;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import simul.infrastructure.MbusNetwork;

/**
 * Created by Federico Falconi on 04/07/2017.
 */
public class Slave extends MbusDevice {

    private int slaveAddress;
    // neighbor -> rumor
    private HashMap<Integer, Double> noiseTable = new HashMap<Integer, Double>();
    private HashMap<Integer, Boolean> updatedNoiseTable = new HashMap<Integer, Boolean>();

    //private slaveCache cache = new slaveCache();
    private int fatherAddress = -1;

    public Slave(MbusNetwork owner, String name, Boolean showInTrace, int pos) {
        super(owner, name, showInTrace, pos);
        slaveAddress = pos;
    }

    public boolean decode(MbusMessage message) {

        if (noiseTable.containsKey(message.getSource()) && !updatedNoiseTable.get(message.getSource())) {
            updatedNoiseTable.put(message.getSource(), message.getErrorRate() != noiseTable.get(message.getSource()));
            if (message.getErrorRate() != noiseTable.get(message.getSource())) {
                Stats.updateNoiseSlave++;
                this.updateLocalNoiseTable++;
            }

        } else if (!noiseTable.containsKey(message.getSource())) {
            updatedNoiseTable.put(message.getSource(), true);// first time.
            Stats.updateNoiseSlave++;
            this.updateLocalNoiseTable++;
        }

        noiseTable.put(message.getSource(), message.getErrorRate());

        return true;
    }

    public Response receive(MbusMessage message) throws CommunicationFault {
        super.receive(message);
        int nextHop = 0;

        Request received = new Request((Request) message);

        if (received.getHopDestination() != slaveAddress) {
            // Alarm
            this.numberOfUpdates++;
            return null;
            // new InternalError("Something was wrong");
        }
        if (message.getErrorRate() == 2) {
            throw new CommunicationFault(this.slaveAddress);
        }

        if (received.getDestination() == this.slaveAddress) { // i'm the destination node. or error.
            //Stats.hopCount+=1;
            ArrayDeque<NoiseTable> tablesList = new ArrayDeque<NoiseTable>();
            //Stats.currentThroughtput += received.isPartialCached() ? (received.getCacheList().size() * 2) + 1 : received.getHopList().size() + 1; // plus the final destination
            Stats.hopCount += 1;
            NoiseTable ns = new NoiseTable(getNoise(),this.slaveAddress);
            if (!ns.getEntries().isEmpty()) {
                tablesList.push(ns);
            }

            //System.out.println("Slave #"+this.slaveAddress+": Message received with payload length:  " + received.getPayloadLen()+ " time: "+presentTime());
            //System.out.print("la tabella è stata trasmessa da " + slaveAddress);
            //System.out.println("Slave #"+this.slaveAddress+" Forward back packet ");
            // trasmit the response package.
            //System.out.println("Sending response to:"+this.fatherAddress);
            if (message.getErrorRate() == 2) {// hit error.
                Response res = (new Response(-received.getCode(), received.getToken(), 20, slaveAddress, this.fatherAddress, tablesList));
                // res.setHop(message.getHop());
                // res.incThroughput(message.getThroughput());
                return res;
            } else {// no error.
                Response res = (new Response(received.getCode(), received.getToken(), 20, slaveAddress, this.fatherAddress, tablesList));
                //res.setHop(message.getHop());
                //res.incThroughput(message.getThroughput());
                return res;
            }

        } else {
            // does not consider the last hop.(destination)
            //message.incHop();
            Stats.hopCount += 1;
            // forward the package.
            // next hop algorithm
            // if the destination is a  neighbor go straight there.
            // Prority: the hop list and after the cache.
            //System.out.println("Slave #"+this.slaveAddress+" Forward packet ");
            // if the destination is a  neighbor go straight there.
            /*if (network.configManager.getNeighbors(this.slaveAddress).contains(received.getDestination())){
                                    nextHop = received.getDestination();
                                    received.getHopList().clear();
                                    cache.addLocalCache(nextHop,received.getDestination()); // Why? Problem? sync with server.
                            } else May short the path .. but it is not optimized for noise.*/
            // !received.isPartialCached()
            if (    received.getHopList().size() > 1
                    && received.getHopList().getFirst() == this.slaveAddress) {
                received.getHopList().pollFirst();// leave me please.
                if (received.getHopList().size() >= 1) {
                    nextHop = received.getHopList().getFirst();
                } else {
                    nextHop = received.getDestination();
                }

                if (nextHop == received.getDestination()) {
                    received.getHopList().clear(); // 
                }
                // update cache.
                //if (nextHop!=received.getDestination()){
                // cache.addLocalCache(nextHop, received.getDestination());
                //}

            }
            /*else {
                if (received.isPartialCached()
                        && received.contains(this.slaveAddress) != -1) {
                    nextHop = received.contains(this.slaveAddress);
                    if (nextHop == received.getDestination()) {
                        received.getCacheList().clear(); // 
                    }
                    received.remove(this.slaveAddress);
                    // update cache.
                    cache.addLocalCache(nextHop, received.getDestination());
                } else if (!received.isPartialCached() && network.configManager.getNeighbors((int) this.slaveAddress).contains(received.getDestination())) {
                    nextHop = received.getDestination();
                    received.setHopList(new ArrayDeque<Integer>());
                    cache.addLocalCache(nextHop, received.getDestination()); // Why? Problem? sync with server.
                } else { // get destination.
                    nextHop = (cache.lookUpCache(received.getDestination()));

                    if (nextHop == received.getDestination()) {
                        received.setHopList(new ArrayDeque<Integer>());
                    }

                    if (nextHop == -1) {
                        throw new InternalError("impossible");
                        //throw new ("Internal error");// not possible 
                    }
                }
            }*/
        }
        // stat after for trasmit reason.
        //Stats.currentThroughtput += received.isPartialCached() ? (received.getCacheList().size() * 2) +1 : received.getHopList().size()+1; // plus the final destination
        // Sending message.
         // received.isPartialCached() ? received.getCacheList() : recevied.getHopList()
        Request req = new Request(received.getCode(), received.getToken(), received.getPayloadLen(), this.slaveAddress, nextHop, received.getDestination(), received.getHopList());
        //System.out.println("Request: Sending from " + req.getSource());
        //System.out.println("Sending to " + nextHop);
        //req.setHop(message.getHop());
        //System.out.println("Slave #"+slaveAddress+": Retrasmit noiseTable");
        Response res = null;
        try{
            res = transmit(req, false);
        }catch(CommunicationFault c){
            updatedNoiseTable.put(req.getHopDestination(), true);
            if (c.getTables().size()==0) {
                noiseTable.put(req.getHopDestination(), 2.0);
                Stats.updateNoiseSlave++;
            }
            c.getTables().push(new NoiseTable(getNoise(),this.slaveAddress));
            throw c;
        }
        
        NoiseTable currentNoiseTable = new NoiseTable(getNoise(),this.slaveAddress);
        if (!currentNoiseTable.getEntries().isEmpty()) {
            res.getNoiseTables().push(currentNoiseTable);
        }

        return res;

    }
    
    public HashMap<Integer, Double> getNoise(){
        HashMap<Integer, Double> nnt = new HashMap<Integer, Double>();

        for (Integer key : noiseTable.keySet()) {
            if (updatedNoiseTable.get(key)) { // fine ok i can add..
                nnt.put(key, noiseTable.get(key));
            }
            updatedNoiseTable.put(key, false);
        }
        return nnt;
    }

    /*
    public void lifeCycle() throws SuspendExecution {
        int fatherAddress = -1;
        /**
         * Wait a package until the 
         * 
         
        while (true) {
            if (getReceived() != null && getReceived().getClass() == Request.class) {
                Request received = new Request((Request)retrieveMsg());

                if (decode(received, received.getSource()) && (received.getHopDestination() == this.slaveAddress)) { // check local destination.
                    fatherAddress = received.getSource(); // Get source address.
                    // stats
                    
                    
                    //System.out.println(received.getHopList().size());
                    
                    if (received.getDestination() == this.slaveAddress) { // i'm the destination node.
                        ArrayDeque<NoiseTable> tablesList = new ArrayDeque<NoiseTable>();
                        tablesList.push(new NoiseTable(noiseTable, this.slaveAddress));
                        
                        //System.out.println("Slave #"+this.slaveAddress+": Message received with payload length:  " + received.getPayloadLen()+ " time: "+presentTime());
                        //System.out.print("la tabella è stata trasmessa da " + slaveAddress);
                        //System.out.println("Slave #"+this.slaveAddress+" Forward back packet ");
                        // trasmit the response package.
                        transmit(new Response(1, received.getToken(), 20, slaveAddress, fatherAddress, tablesList),
                                false);
                    } else {
                    	// does not consider the last hop.(destination)s
                    	this.network.hopCount++; 
                    	
                    	// forward the package.
                    	// next hop algorithm
                    	// if the destination is a  neighbor go straight there.
                    	// Prority: the hop list and after the cache.
                    	//System.out.println("Slave #"+this.slaveAddress+" Forward packet ");
                    	// if the destination is a  neighbor go straight there.
                    	int nextHop;
                    	/*if (network.configManager.getNeighbors(this.slaveAddress).contains(received.getDestination())){
                    		nextHop = received.getDestination();
                    		received.getHopList().clear();
                    		cache.addLocalCache(nextHop,received.getDestination()); // Why? Problem? sync with server.
                    	} else May short the path .. but it is not optimized for noise.
                    	if (received.getHopList().size() > 1  && received.getHopList().getFirst() == this.slaveAddress){
                    		received.getHopList().pollFirst();
                    		nextHop = received.getHopList().getFirst();
                    		
                    		if (nextHop == received.getDestination()){
                    			received.getHopList().clear(); // 
                    		}
                    		// update cache.
                    		cache.addLocalCache(nextHop,received.getDestination());
                    	}else if (network.configManager.getNeighbors(this.slaveAddress).contains(received.getDestination())){
                    		nextHop = received.getDestination();
                    		received.getHopList().clear();
                    		cache.addLocalCache(nextHop,received.getDestination()); // Why? Problem? sync with server.
                    	} else { // get destination.
                    		nextHop = (cache.lookUpCache(received.getDestination()));
                    		
                    		if (nextHop == received.getDestination()){
                    			received.getHopList().clear();
                    		}
                    		
                    		if (nextHop==-1){
                    			System.out.println("impossible");
                    			//throw new ("Internal error");// not possible 
                    		}
                    	}
                    	// stat after for trasmit reason.
                    	this.network.currentThroughtput += received.getHopList().size() + 1; // plus the final destination 
                    	
                        transmit(new Request(received.getCode(), received.getToken(), received.getPayloadLen(),
                        		this.slaveAddress,nextHop,received.getDestination(), received.getHopList()), false);
                    }
                }
            } else if (getReceived() != null &&getReceived().getClass() == Response.class) { // Response ..
                Response received = new Response((Response)retrieveMsg());

                if (decode(received, received.getSource()) && (received.getNextHop() == slaveAddress)) {
                    assert (fatherAddress != -1);

                    received.getNoiseTables().push(new NoiseTable(noiseTable, slaveAddress));

                    //System.out.println("Slave #"+slaveAddress+": Retrasmit noiseTable");
                    transmit(new Response(received.getCode(), received.getToken(), received.getPayloadLen(),
                            slaveAddress, fatherAddress, received.getNoiseTables()), false);
                }
            }

        }
    }*/

    public void updateLocalNoise() {
        HashMap<Integer, Double> outgoingEdges = network.getOutgoingEdges(this.slaveAddress); // get all neighbors.
        this.noiseTable.clear();
        for (Integer key : outgoingEdges.keySet()) {// send to all neighbors.
            this.noiseTable.put(key, outgoingEdges.get(key));
        }

    }

}
