package org.wmbus.protocol.messages.formats;


import org.wmbus.protocol.simulation.WMBusSimulation;

public abstract class MBusMessageFormatB  extends MBusMessageFormatCore{
    private int preamble_size;

    public MBusMessageFormatB(WMBusSimulation simulation) {
        super(simulation);
        this.preamble_size = simulation.getwMbusConfig().getHeaderSize();
    }

    public int getBlockSize(int n){
        if (n==0){
            return 10+preamble_size+1+1;
        }else if (n==1){
            return 118*8;
        }else {
            return 126*8;
        }
    }
    public double computeFullFrameSizeOnlyPayload(int n){
        /* A block for header. */
        int headerBlockSize = this.getBlockSize(0);
        int dataBlockSize = this.getBlockSize(1);
        int dataBlockPayloadSize = this.getPayloadBlockSize(1);
        int dataBlockHeaderSize = this.getHeaderBlockSize(1);
        int data = headerBlockSize;

        n*=8;
        while(n>dataBlockPayloadSize){
            data+=dataBlockPayloadSize;
            n-=dataBlockPayloadSize;
        }
        if (n>0){
            data+=dataBlockPayloadSize;
        }
        return data;
    }

    public double computeECC(double ber){
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
            if (result > 1){
                return Integer.MAX_VALUE;
            }
        }
        return hammingResultOneCount/this.getMessageBlockCount();
    }

    public int getPayloadBlockSize(int n){
        if (n==0){
            return 0;
        }else if (n==1){
            return 115*8;
        }else{
            return 126*8;
        }
    }
    public int getHeaderBlockSize(int n){
        if (n==0){
            return 10+preamble_size+1+1;
        }else if (n==1){
            return 2*8;
        }else {
            return 2*8;
        }
    }
    @Override
    public int getMessageHeader(){
        return this.getHeaderBlockSize(0);
    }

    public double computeFullFrameSize(int n){
        /* A block for header. */
        int headerBlockSize = this.getBlockSize(0);
        int dataBlockSize = this.getBlockSize(1);
        int dataBlockPayloadSize = this.getPayloadBlockSize(1);
        n*=8;
        double data = headerBlockSize;
        while(n>dataBlockPayloadSize){
            data+=dataBlockSize;
            n-=dataBlockPayloadSize;
        }
        if (n>0){
            data+=n+(dataBlockSize-dataBlockPayloadSize);
        }
        return data;
    }

    public int computeFullFrameCount(int n){
        /* A block for header. */
        int headerBlockSize = this.getBlockSize(0);
        int dataBlockSize = this.getBlockSize(1);
        int dataBlockPayloadSize = this.getPayloadBlockSize(1);
        int dataBlockCount = 1;
        n*=8;
        while(n>dataBlockPayloadSize){
            dataBlockCount++;
            n-=dataBlockPayloadSize;
        }
        if (n>0){
            dataBlockCount++;
        }
        return dataBlockCount;
    }


}
