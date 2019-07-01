package simul.nodes;

//import simul.cache.slaveCache;
//import simul.cache.base.cachePair;
import org.pmw.tinylog.Logger;
import simul.messages.CommunicationState;
import simul.messages.MbusMessage;

import java.util.ArrayDeque;

import simul.infrastructure.MbusNetwork;
import simul.messages.Request;
import simul.messages.Response;
import simul.infrastructure.ECCTable;
import simul.protocol.SimulationConfiguration;

/**
 * Created by Federico Falconi on 04/07/2017.
 */
public class Slave extends MbusDevice {
    private int previousHop = -1;
    private int nextHop= -1;

    public Slave(MbusNetwork owner, int pos) {
        super(owner, pos);
    }
    /*
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
    */
    @Override
    public void receive(MbusMessage message)  {
        super.receive(message);
        double tras_result = 0; // fixed value.
        // Stats.hopCount += 1;

        // Check message type.
        if (message.getMessageType()== SimulationConfiguration.PACKET_REQUEST) {
            Request req = (Request) message;
            // here delete last element of queue.
            int size = req.getHops().size();
            previousHop = message.getSource();
            nextHop = message.getDestination();

            if (size==1) {
                // I'm the destination node.
                ArrayDeque<ECCTable> ECCTable = new ArrayDeque<ECCTable>();
                Response res_req = new Response(this.getNodeID(),previousHop,this.attachECCTable(ECCTable));
                res_req.setData(10);
                tras_result = this.transmit(res_req);
            }else {
                // just relay node.
                req.getHops().pollFirst();


                tras_result = this.transmit(req);
            }


            } else
                if (message.getMessageType()== SimulationConfiguration.PACKET_RESPONSE) {
                    Response res = (Response) message;
                    Logger.trace("Data: "+((Response) message).getData());
                    Response new_res = new Response(this.getNodeID(),previousHop,this.attachECCTable(res.getECCTables()));
                    new_res.setData(res.getData());
                    tras_result = this.transmit(new_res);

                    if (tras_result==CommunicationState.TIMEOUT){
                        // Workaround without timer.
                        // Manually trigger timeout for a node.
                        this.network.getNode(previousHop).triggerTimeout();
                    }
        }


    }
    @Override
    public void triggerTimeout(){
        super.triggerTimeout();
        ArrayDeque<ECCTable> ECCTable = new ArrayDeque<ECCTable>();
        Response res_req = new Response(this.getNodeID(),previousHop,this.attachECCTable(ECCTable));
        double tras_result = this.transmit(res_req);
        Logger.trace("Packet loss");
        if (tras_result==CommunicationState.TIMEOUT){
            // Workaround without timer.
            // Manually trigger timeout for a node.
            this.network.getNode(this.previousHop).triggerTimeout();
        }
    }


}
