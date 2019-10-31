package org.wmbus.protocol.messages;

import org.wmbus.simulation.WMBusSimulation;

public class CustomSizeMessage extends WMbusMessage {
    private final int customSize;
    private final int destination;

    public CustomSizeMessage(WMBusSimulation simulation, int source, int destination, int customSize){
        super(simulation,source);
        this.destination = destination;
        this.customSize = customSize;
    }
    /**
     * Not needed (used in simulation)
     * @return
     */
    @Override
    public int getDestination() {
        return this.destination;
    }

    @Override
    public int getMessageSize() {
        return customSize;
    }
    /**
     * Not needed (used in simulation)
     * @return
     */
    @Override
    public int getMessageSizeOnlyPayloadWithParitybit() {
        return 0;
    }

    /**
     * Not needed (used in simulation)
     * @return
     */
    @Override
    public int getMessageSizeOnlyPayloadWithoutParitybit() {
        return 0;
    }

    @Override
    public int getMessageBlockCount() {
        return this.computeFullFrameCount(this.getMessageSize());
    }
}
