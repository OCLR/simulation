package simul.messages.formats;

import simul.protocol.SimulationConfiguration;

public abstract class MBusMessageFormatCore {

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



    private static double powerN(double base, long n) {
        double result = 1;
        for (int i = 0; i < n; i++) {
            result *= base;
        }
        return result;
    }

    public static int getNumberOfBits(double numberbyte){
        int nbytes = (int) numberbyte;
        int addedbit = (int) ((numberbyte - nbytes)*10);
        return (nbytes*8)+addedbit;
    }

    public int computeHamming(long n,double ber){
        double neg_ber = (1-ber);

        double noerror= MBusMessageFormatCore.powerN(1-ber,n); // (1-r)^n
        double oneerror= MBusMessageFormatCore.powerN(neg_ber,n-1)*n*ber; // (1-r)^(n-1)*n*ber
        double morethanoneerror = 1-(noerror+oneerror);
        double randomValue = SimulationConfiguration.CONF_RANDOM.nextDouble();
        if (randomValue <=noerror){
            return 0;
        }else if(SimulationConfiguration.CONF_HAMMING == true && randomValue <=noerror+oneerror){
            return 1;
        }else {
            return Integer.MAX_VALUE;
        }
    }

    public static double getSuccessProb(long n,double ber){
        double neg_ber = (1-ber);

        double noerror= MBusMessageFormatCore.powerN(1-ber,n); // (1-r)^n
        double oneerror= MBusMessageFormatCore.powerN(neg_ber,n-1)*n*ber; // (1-r)^(n-1)*n*ber
        double morethanoneerror = 1-(noerror+oneerror);
        double randomValue = SimulationConfiguration.CONF_RANDOM.nextDouble();
        return noerror;
    }
    public static double getRecoverableProb(long n,double ber){
        double neg_ber = (1-ber);

        double noerror= MBusMessageFormatCore.powerN(1-ber,n); // (1-r)^n
        double oneerror= MBusMessageFormatCore.powerN(neg_ber,n-1)*n*ber; // (1-r)^(n-1)*n*ber
        double morethanoneerror = 1-(noerror+oneerror);
        double randomValue = SimulationConfiguration.CONF_RANDOM.nextDouble();
        return oneerror;
    }
    public static double getFaultProb(long n,double ber){
        double neg_ber = (1-ber);
        double noerror= MBusMessageFormatCore.powerN(1-ber,n); // (1-r)^n
        double oneerror= MBusMessageFormatCore.powerN(neg_ber,n-1)*n*ber; // (1-r)^(n-1)*n*ber
        double morethanoneerror = 1-(noerror+oneerror);
        double randomValue = SimulationConfiguration.CONF_RANDOM.nextDouble();
        if (SimulationConfiguration.CONF_HAMMING){
            return  morethanoneerror;
        }else{
            return  oneerror+morethanoneerror;
        }

    }

}
