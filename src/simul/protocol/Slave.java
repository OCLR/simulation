package simul.protocol;

import co.paralleluniverse.fibers.SuspendExecution;
import desmoj.core.simulator.Model;

import desmoj.core.simulator.TimeSpan;
import simul.cache.slaveCache;
import simul.cache.base.cachePair;
import simul.infrastructure.MbusDevice;
import simul.infrastructure.MbusMessage;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Federico Falconi on 04/07/2017.
 */
public class Slave extends MbusDevice {
    private int slaveAddress;
    // neighbor -> rumor
    private HashMap<Integer, Double>  noiseTable = new HashMap<Integer, Double>();
    private slaveCache cache = new slaveCache();
    

    public Slave(Model owner, String name, Boolean showInTrace, int pos) {
        super(owner, name, showInTrace, pos);
        slaveAddress = pos;
    }


    private boolean decode(MbusMessage message, int source) throws SuspendExecution{
        double sum = 0;
        boolean integrity = true;

        hold(new TimeSpan(message.getLength()));

        if ((message.getErrors(1) < 2) && (message.getErrors(2) < 2)) {
            sum += (message.getErrors(1) + message.getErrors(2));
        }
        else {
            return false;
        }

        for (int i = 0; i < message.getLength(); i++) {
            if (message.getErrors(i) > 1) {
                integrity = false;
                sum += 2;
            }
            else if (message.getErrors(i) == 1) {
                sum += 1;
            }
        }

        noiseTable.put(source, sum / message.getLength());

        if (!integrity) {
            if (message.getClass() == Request.class) {
                /*if(((Request)message).getHopList().getFirst() == slaveAddress) {
                    System.out.println("Il nodo " + slaveAddress + " ha ricevuto il pacchetto ma era danneggiato");
                }*/
            }
            else {
                /*if(((Response)message).getNextHop() == slaveAddress) {
                    System.out.println("Il nodo " + slaveAddress + " ha ricevuto il pacchetto ma era danneggiato");
                }*/
            }
        }

        return integrity;
    }
    

    public void lifeCycle() throws SuspendExecution {
        int fatherAddress = -1;
        /**
         * Wait a package until the 
         * 
         */
        while (true) {
            if (getReceived() != null && getReceived().getClass() == Request.class) {
                Request received = new Request((Request)retrieveMsg());

                if (decode(received, received.getSource()) && (received.getHopDestination() == this.slaveAddress)) { // check local destination.
                    fatherAddress = received.getSource(); // Get source address.

                    if (received.getDestination() == this.slaveAddress) { // i'm the destination node.
                        ArrayDeque<NoiseTable> tablesList = new ArrayDeque<NoiseTable>();
                        tablesList.push(new NoiseTable(noiseTable, this.slaveAddress));

                        //System.out.println("Slave #"+this.slaveAddress+": Message received with payload length:  " + received.getPayloadLen()+ " time: "+presentTime());
                        //System.out.print("la tabella è stata trasmessa da " + slaveAddress);
                        System.out.println("Slave #"+this.slaveAddress+" Forward back packet ");
                        // trasmit the response package.
                        transmit(new Response(1, received.getToken(), 20, slaveAddress, fatherAddress, tablesList),
                                false);
                    } else {
                    	// forward the package.
                    	// next hop algorithm
                    	// if the destination is a  neighbor go straight there.
                    	// Prority: the hop list and after the cache.
                    	System.out.println("Slave #"+this.slaveAddress+" Forward packet ");
                    	// if the destination is a  neighbor go straight there.
                    	int nextHop;
                    	if (network.configManager.getNeighbors(this.slaveAddress).contains(received.getDestination())){
                    		nextHop = received.getDestination();
                    		cache.addLocalCache(nextHop,received.getDestination()); // Why? Problem? sync with server.
                    	} else
                    	if (received.getHopList().size() > 1  && received.getHopList().getFirst() == this.slaveAddress){
                    		received.getHopList().pollFirst();
                    		nextHop = received.getHopList().getFirst();
                    		// update cache.
                    		cache.addLocalCache(nextHop,received.getDestination());
                    	}else { // get destination.
                    		nextHop = (cache.lookUpCache(received.getDestination()));
                    		if (nextHop==-1){
                    			System.out.println("impossible");
                    			//throw new ("Internal error");// not possible 
                    		}
                    	}
                    	
                        transmit(new Request(received.getCode(), received.getToken(), received.getPayloadLen(),
                        		this.slaveAddress,nextHop,received.getDestination(), received.getHopList()), false);
                    }
                }
            } else if (getReceived() != null &&getReceived().getClass() == Response.class) { // Response ..
                Response received = new Response((Response)retrieveMsg());

                if (decode(received, received.getSource()) && (received.getNextHop() == slaveAddress)) {
                    assert (fatherAddress != -1);

                    received.getNoiseTables().push(new NoiseTable(noiseTable, slaveAddress));

                    System.out.println("Slave #"+slaveAddress+": Retrasmit noiseTable");
                    transmit(new Response(received.getCode(), received.getToken(), received.getPayloadLen(),
                            slaveAddress, fatherAddress, received.getNoiseTables()), false);
                }
            }

            passivate();
        }
    }


	

}
