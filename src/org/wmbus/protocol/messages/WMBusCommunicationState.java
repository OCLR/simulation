package org.wmbus.protocol.messages;


public class WMBusCommunicationState {
    public static final int TIMEOUT = 255;
    public static final double NOT_FOR_ME = -1;
    public static final boolean isOK(double ecc){
        return ecc >= 0 && ecc <= 254;
    }
}

