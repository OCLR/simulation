package simul.messages.formats;

import simul.protocol.SimulationConfiguration;

/**
 * Payload format
 * (7,4)
 *  4 bit data +  3 bit for hamming code.
 */
public abstract class MBusMessageFormatCustom extends MBusMessageFormatCore {
    private int preamble_size = SimulationConfiguration.CONF_PREHEADER;
    /*
    * Everything expressed in bit.
    *
    * */
    @Override
    public int getMessageHeader(){
        return this.getHeaderBlockSize(0);
    }

    public int getBlockSize(int n){
        if (n==0){
            return (12+preamble_size+1+1)*8;
        }else{
            return 7;
        }
    }

    public int getPayloadBlockSize(int n){
        if (n==0){
            return 0;
        }else{
            return 4;
        }
    }

    public int getHeaderBlockSize(int n){
        if (n==0){
            return (12+preamble_size+1+1)*8;
        }else{
            return 3;
        }
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

    public int computeFullFrameSize(int n){
        /* A block for header. */
        int headerBlockSize = this.getBlockSize(0);
        int dataBlockSize = this.getBlockSize(1);
        int dataBlockPayloadSize = this.getPayloadBlockSize(1);
        int data = headerBlockSize;
        int ndata = n;
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

    public int computeFullFrameSizePayloadWithoutParityBit(int n){
        /* A block for header. */
        int headerBlockSize = this.getBlockSize(0);
        int dataBlockSize = this.getBlockSize(1);
        int dataBlockPayloadSize = this.getPayloadBlockSize(1);
        int dataBlockHeaderSize = this.getHeaderBlockSize(1);
        int data = 0;
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
    public int computeFullFrameSizePayloadWithParityBit(int n){
        /* A block for header. */
        int headerBlockSize = this.getBlockSize(0);
        int dataBlockSize = this.getBlockSize(1);
        int dataBlockPayloadSize = this.getPayloadBlockSize(1);
        int dataBlockHeaderSize = this.getHeaderBlockSize(1);
        int data = 0;
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
