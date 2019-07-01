package simul.messages;

import simul.protocol.SimulationConfiguration;

/**
 * @deprecated
 */
public class RequestAck  extends  MbusMessage{
    private byte type = SimulationConfiguration.PACKET_REQUESTACK;
    private int destination = 0;

    public RequestAck(int source,int destination) {
        super(source);
        this.destination = destination;
    }



    @Override
    public int getDestination() {
        return this.destination;
    }

    @Override
    public int getMessageSize() {
        return 0;
    }

    @Override
    public int getMessageSizeOnlyPayloadWithParitybit() {
        return 0;
    }

    @Override
    public int getMessageSizeOnlyPayloadWithoutParitybit() {
        return 0;
    }

    @Override
    public int getMessageBlockCount() {
        return 0;
    }

    @Override
    public double computeECC(double ber) {
        return 0;
    }

}
