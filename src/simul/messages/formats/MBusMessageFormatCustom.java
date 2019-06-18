package simul.messages.formats;

import simul.protocol.SimulationConfiguration;

public abstract class MBusMessageFormatCustom extends MBusMessageFormatCore {
    private int preamble_size = SimulationConfiguration.CONF_PREHEADER;

    public int getBlockSize(int n){
        if (n==0){
            return 12+preamble_size+1;
        }else{
            return 18;
        }
    }

    public int getPayloadBlockSize(int n){
        if (n==0){
            return 0;
        }else{
            return 15;
        }
    }

    public int computeFullFrameSize(int n){
        /* A block for header. */
        int headerBlockSize = this.getBlockSize(0);
        int dataBlockSize = this.getBlockSize(1);
        int dataBlockPayloadSize = this.getPayloadBlockSize(1);
        int data = headerBlockSize;
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
