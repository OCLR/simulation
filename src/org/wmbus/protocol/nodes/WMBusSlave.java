package org.wmbus.protocol.nodes;//import wmbus.cache.slaveCache;
//import wmbus.cache.base.cachePair;

import org.pmw.tinylog.Logger;
import org.wmbus.protocol.infrastructure.ECCTable;
import org.wmbus.protocol.messages.*;
import org.wmbus.simulation.WMBusSimulation;

import java.util.ArrayDeque;

/**
 * Created by Federico Falconi on 04/07/2017.
 */
public class WMBusSlave extends WMbusDevice {
    private int previousHop = -1;
    private int nextHop= -1;

    public WMBusSlave(WMBusSimulation simulation, int pos) {
        super(simulation, pos);
    }
    /*
    public boolean decode(WMbusMessage message) {

        if (noiseTable.containsKey(message.getSource()) && !updatedNoiseTable.get(message.getSource())) {
            updatedNoiseTable.put(message.getSource(), message.getErrorRate() != noiseTable.get(message.getSource()));
            if (message.getErrorRate() != noiseTable.get(message.getSource())) {
                WMBusStats.updateNoiseSlave++;
                this.updateLocalNoiseTable++;
            }

        } else if (!noiseTable.containsKey(message.getSource())) {
            updatedNoiseTable.put(message.getSource(), true);// first time.
            WMBusStats.updateNoiseSlave++;
            this.updateLocalNoiseTable++;
        }

        noiseTable.put(message.getSource(), message.getErrorRate());

        return true;
    }
    */
    @Override
    public void receive(WMbusMessage message)  {
        super.receive(message);
        double tras_result = 0; // fixed value.
        // WMBusStats.hopCount += 1;

        // Check message type.
        if (message.getMessageType()== WMBusPacketType.PACKET_REQUEST) {
            Request req = (Request) message;
            // here delete last element of queue.
            int size = req.getHops().size();
            previousHop = message.getSource();
            nextHop = message.getDestination();

            if (size==1) {
                // I'm the destination node.
                ArrayDeque<ECCTable> ECCTable = new ArrayDeque<ECCTable>();
                Response res_req = new Response(this.simulation,this.getNodeID(),previousHop,this.attachECCTable(ECCTable));
                res_req.setData(10);
                tras_result = this.transmit(res_req);
            }else {
                // just relay node.
                req.getHops().remove(0);


                tras_result = this.transmit(req);
            }


            } else
                if (message.getMessageType()== WMBusPacketType.PACKET_RESPONSE) {
                    Response res = (Response) message;
                    Logger.trace("Data: "+((Response) message).getData());
                    Response new_res = new Response(this.simulation,this.getNodeID(),previousHop,this.attachECCTable(res.getECCTables()));
                    new_res.setData(res.getData());
                    tras_result = this.transmit(new_res);

                    if (tras_result== WMBusCommunicationState.TIMEOUT){
                        // Workaround without timer.
                        // Manually trigger timeout for a node.
                        this.simulation.getwMbusNetwork().getNode(previousHop).triggerTimeout();
                    }
        }


    }
    @Override
    public void triggerTimeout(){
        super.triggerTimeout();
        ArrayDeque<ECCTable> ECCTable = new ArrayDeque<ECCTable>();
        Response res_req = new Response(this.simulation,this.getNodeID(),previousHop,this.attachECCTable(ECCTable));
        double tras_result = this.transmit(res_req);
        Logger.trace("Packet loss");
        if (tras_result== WMBusCommunicationState.TIMEOUT){
            // Workaround without timer.
            // Manually trigger timeout for a node.
            this.simulation.getwMbusNetwork().getNode(this.previousHop).triggerTimeout();
        }
    }


}
