package org.wmbus.protocol.messages.formats;


import org.wmbus.protocol.simulation.WMBusSimulation;

public abstract class MBusMessageFormatA extends MBusMessageFormatCore {
    private int preamble_size;

    public MBusMessageFormatA(WMBusSimulation simulation) {
        super(simulation);
        this.preamble_size = simulation.getwMbusConfig().getHeaderSize();
    }

    @Override
    public int getMessageHeader(){
        return this.getHeaderBlockSize(0);
    }

    public int getBlockSize(int n){
        if (n==0){
            return (12+preamble_size+1)*8;
        }else{
            return 18*8;
        }
    }

    public int getPayloadBlockSize(int n){
        if (n==0){
            return 0;
        }else{
            return 15*8;
        }
    }
    public int getHeaderBlockSize(int n){
        if (n==0){
            return (12+preamble_size+1);
        }else if (n==1){
            return 2*8;
        }else {
            return 2*8;
        }
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

    public double computeFullFrameSize(int n){
        /* A block for header. */
        int headerBlockSize = this.getBlockSize(0);
        int dataBlockSize = this.getBlockSize(1);
        int  dataBlockPayloadSize = this.getPayloadBlockSize(1);
        int data = headerBlockSize;
        n*=8;
        while(n>dataBlockPayloadSize){
            data+=dataBlockSize;
            n-=dataBlockPayloadSize;
        }
        if (n>0){
            data+=dataBlockSize;
        }
        return data;
    }

    public double computeFullFrameCount(int n){
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
