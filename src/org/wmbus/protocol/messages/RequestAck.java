package org.wmbus.protocol.messages;


import org.wmbus.simulation.WMBusSimulation;

/**
 * @Deprecated
 */
public class RequestAck  extends WMbusMessage {
    private byte type = WMBusPacketType.PACKET_REQUESTACK;
    private int destination = 0;

    public RequestAck(WMBusSimulation simulation, int source, int destination) {
        super(simulation,source);
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
    public int computeECC(double ber) {
        return 0;
    }

}
