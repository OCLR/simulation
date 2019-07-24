package org.wmbus.protocol.messages;


public class WMBusCommunicationState {
    public static final double TIMEOUT = Integer.MAX_VALUE;
    public static final double NOT_FOR_ME = 2;
    public static final boolean isOK(double ecc){
        return ecc >=0 && ecc <=1;
    }
}

