package org.wmbus.protocol.messages;

import org.wmbus.protocol.messages.formats.MBusMessageFormatCustom;
import org.wmbus.simulation.WMBusSimulation;


public abstract class WMbusMessage extends MBusMessageFormatCustom {
    // Block 1 information.
    //private byte lfield = 0;
    //private byte cfield = 0;
    // private short mfield = 0;
    /* A-field */
    private int  source = 0;
    //private byte type   = 0;
    //private byte version = 0;
    /* CRC-field */
    //private short ecc = 0;

    // postamble part in common between blocks.
    // 1 byte

    public WMbusMessage(WMBusSimulation simulation, int source){
        super(simulation);
        this.source = source;
    }


    public void setSource(int source) {
        this.source = source;
    }

    public int getMessageType(){
        return WMBusPacketType.PACKET_GENERIC;
    }

    public int getSource(){
        return this.source;
    }

    public String toString(){
        return "Source:"+this.source;
    }

    public abstract int getDestination();


}