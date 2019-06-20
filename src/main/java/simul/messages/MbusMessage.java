package simul.messages;

import simul.messages.formats.MBusMessageFormatA;
import simul.messages.formats.MBusMessageFormatB;
import simul.messages.formats.MBusMessageFormatCustom;
import simul.protocol.SimulationConfiguration;

/**
 * Created by Federico Falconi on 04/07/2017.
 */
public abstract class MbusMessage extends MBusMessageFormatCustom {
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

    public MbusMessage(int source){
        this.source = source;
    }


    public void setSource(int source) {
        this.source = source;
    }

    public int getMessageType(){
        return SimulationConfiguration.PACKET_GENERIC;
    }

    public int getSource(){
        return this.source;
    }

    public String toString(){
        return "Source:"+this.source;
    }

    public abstract int getDestination();


}