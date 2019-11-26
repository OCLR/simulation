package org.wmbus.protocol.messages.formats;


import org.pmw.tinylog.Logger;
import org.wmbus.simulation.WMBusSimulation;


public abstract class MBusMessageFormatCore {

    protected WMBusSimulation simulation;

    public MBusMessageFormatCore(WMBusSimulation simulation) {
        this.simulation = simulation;
    }

    public abstract int getMessageSize();

    public abstract int getMessageHeader();


    public abstract int getMessageSizeOnlyPayloadWithParitybit();

    public abstract int getMessageSizeOnlyPayloadWithoutParitybit();

    public abstract int getMessageBlockCount();

    public abstract int getBlockSize(int n);

    /*public double computeECC(double ber,int skipFirstBlocks){
        // int packetSize = this.getSize();
        // packetSize-=2;
        double hammingResultOneCount = 0;
        int result;

        for (int i = 0; i < skipFirstBlocks; i++) {
            result = this.computeHamming(this.getBlockSize(0),ber);
            //if (result == 1){
            //hammingResultOneCount+=1;
            //}
            if (result > 1){
                return Integer.MAX_VALUE;
            }
        }

        int size = this.getBlockSize(1);

        for (int i = 1; i < this.getMessageBlockCount();i++){
            result = this.computeHamming(size,ber);
            if (result == 1){
                hammingResultOneCount+=1;
            }
            if (result > 1){
                return Integer.MAX_VALUE;
            }
        }
        return hammingResultOneCount/this.getMessageBlockCount();
    }*/

    public static int getNumberOfBits(double numberbyte){
        int nbytes = (int) numberbyte;
        int addedbit = (int) ((numberbyte - nbytes)*10);
        return (nbytes*8)+addedbit;
    }

    public int computeHamming(long n,double ber){
        double neg_ber = (1-ber);

        double noerror= Math.pow(1-ber,n); // (1-r)^n
        double oneerror= Math.pow(neg_ber,n-1)*n*ber; // (1-r)^(n-1)*n*ber
        double morethanoneerror = 1-(noerror+oneerror);
        double randomValue = this.simulation.getwMbusSimulationConfig().CONF_RANDOM.nextDouble();
        if (this.simulation.getwMbusSimulationConfig().CONF_HAMMING){
            Logger.debug("Probability Success Rec Fail: " + noerror+ " "+oneerror+ " "+(1-(noerror+oneerror)) );
        } else {
            Logger.debug("Probability Success Fail: " + noerror+  " "+(1-(noerror)) );
        }

        Logger.debug("Probability Success: " + noerror);

        if (randomValue <=noerror){
            return 0;
        }else if(this.simulation.getwMbusSimulationConfig().CONF_HAMMING && randomValue <=noerror+oneerror){
            return 1;
        }else {
            return Integer.MAX_VALUE;
        }
    }


}
