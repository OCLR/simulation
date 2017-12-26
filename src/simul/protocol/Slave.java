package simul.protocol;

import co.paralleluniverse.fibers.SuspendExecution;
import desmoj.core.simulator.Model;

import desmoj.core.simulator.TimeSpan;
import simul.infrastructure.MbusDevice;
import simul.infrastructure.MbusMessage;

import java.util.ArrayDeque;
import java.util.HashMap;

/**
 * Created by Federico Falconi on 04/07/2017.
 */
public class Slave extends MbusDevice {
    private int slaveAddress;
    private HashMap<Integer, Double> noiseTable = new HashMap<Integer, Double>();

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
                if(((Request)message).getHopList().getFirst() == slaveAddress) {
                    System.out.println("Il nodo " + slaveAddress + " ha ricevuto il pacchetto ma era danneggiato");
                }
            }
            else {
                if(((Response)message).getNextHop() == slaveAddress) {
                    System.out.println("Il nodo " + slaveAddress + " ha ricevuto il pacchetto ma era danneggiato");
                }
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
            if (getReceived().getClass() == Request.class) {
                Request received = new Request((Request)retrieveMsg());

                if (decode(received, received.getSource()) && (received.getHopList().pollFirst() == slaveAddress)) { // check local destination.
                    fatherAddress = received.getSource(); // Get source address.

                    if (received.getHopList().isEmpty()) { // i'm the destination node.
                        ArrayDeque<NoiseTable> tablesList = new ArrayDeque<NoiseTable>();
                        tablesList.push(new NoiseTable(noiseTable, slaveAddress));

                        //System.out.println("Slave #"+this.slaveAddress+": Message received with payload length:  " + received.getPayloadLen()+ " time: "+presentTime());
                        //System.out.print("la tabella è stata trasmessa da " + slaveAddress);
                        System.out.println("Slave #"+this.slaveAddress+" Forward packet ");
                        // trasmit the response package.
                        transmit(new Response(1, received.getToken(), 20, slaveAddress, fatherAddress, tablesList),
                                false);
                    } else {
                    	// forward the package.
                        transmit(new Request(received.getCode(), received.getToken(), received.getPayloadLen(),
                                slaveAddress, received.getHopList()), false);
                    }
                }
            } else if (getReceived().getClass() == Response.class) { // Response ..
                Response received = new Response((Response)retrieveMsg());

                if (decode(received, received.getSource()) && (received.getNextHop() == slaveAddress)) {
                    assert (fatherAddress != -1);

                    received.getNoiseTables().push(new NoiseTable(noiseTable, slaveAddress));

                    System.out.print("Slave #"+slaveAddress+": Retrasmit noiseTable");
                    transmit(new Response(received.getCode(), received.getToken(), received.getPayloadLen(),
                            slaveAddress, fatherAddress, received.getNoiseTables()), false);
                }
            }

            passivate();
        }
    }

}
