package simul.messages.formats;

import simul.messages.MbusMessage;
import simul.protocol.SimulationConfiguration;

public abstract class MBusMessageFormatCore {

    public abstract int getMessageSize();

    public abstract int getMessageBlockCount();

    public abstract int getBlockSize(int n);

    public double computeECC(float ber,int skipFirstBlocks){
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
    }

    public double computeECC(float ber){
        // int packetSize = this.getSize();
        // packetSize-=2;
        double hammingResultOneCount = 0;

        int result = this.computeHamming(this.getBlockSize(0),ber);
        //if (result == 1){
        //hammingResultOneCount+=1;
        //}
        if (result > 1){
            return Integer.MAX_VALUE;
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
    }

    private static float powerN(float base, int n) {
        float result = 1;
        for (int i = 0; i < n; i++) {
            result *= base;
        }
        return result;
    }

    public int computeHamming(int n,float ber){
        float neg_ber = (1-ber);
        float noerror= MBusMessageFormatCore.powerN(1-ber,n); // (1-r)^n
        float oneerror= MBusMessageFormatCore.powerN(neg_ber,n-1)*n*ber; // (1-r)^(n-1)*n*ber
        float morethanoneerror = 1-(noerror+oneerror);
        float randomValue = SimulationConfiguration.CONF_RANDOM.nextFloat();
        if (randomValue <=noerror){
            return 0;
        }else if(SimulationConfiguration.CONF_HAMMING == true && randomValue <=noerror+oneerror){
            return 1;
        }else {
            return Integer.MAX_VALUE;
        }
    }

    public static float getSuccessProb(int n,float ber){
        float neg_ber = (1-ber);
        float noerror= MBusMessageFormatCore.powerN(1-ber,n); // (1-r)^n
        float oneerror= MBusMessageFormatCore.powerN(neg_ber,n-1)*n*ber; // (1-r)^(n-1)*n*ber
        float morethanoneerror = 1-(noerror+oneerror);
        float randomValue = SimulationConfiguration.CONF_RANDOM.nextFloat();
        return noerror;
    }
    public static float getRecoverableProb(int n,float ber){
        float neg_ber = (1-ber);
        float noerror= MBusMessageFormatCore.powerN(1-ber,n); // (1-r)^n
        float oneerror= MBusMessageFormatCore.powerN(neg_ber,n-1)*n*ber; // (1-r)^(n-1)*n*ber
        float morethanoneerror = 1-(noerror+oneerror);
        float randomValue = SimulationConfiguration.CONF_RANDOM.nextFloat();
        return oneerror;
    }
    public static float getFaultProb(int n,float ber){
        float neg_ber = (1-ber);
        float noerror= MBusMessageFormatCore.powerN(1-ber,n); // (1-r)^n
        float oneerror= MBusMessageFormatCore.powerN(neg_ber,n-1)*n*ber; // (1-r)^(n-1)*n*ber
        float morethanoneerror = 1-(noerror+oneerror);
        float randomValue = SimulationConfiguration.CONF_RANDOM.nextFloat();
        return  morethanoneerror;
    }

}
