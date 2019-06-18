package simul.messages.formats;

import simul.protocol.SimulationConfiguration;

public abstract class MBusMessageFormatB  extends MBusMessageFormatCore{
    private int preamble_size = SimulationConfiguration.CONF_PREHEADER;



    public int getBlockSize(int n){
        if (n==0){
            return 10+preamble_size+1;
        }else if (n==1){
            return 118;
        }else {
            return 126;
        }
    }

    public int getPayloadBlockSize(int n){
        if (n==0){
            return 0;
        }else if (n==1){
            return 115;
        }else{
            return 126;
        }
    }

    int computeFullFrameSize(int n){
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
            data+=n+(dataBlockSize-dataBlockPayloadSize);
        }
        return data;
    }

    int computeFullFrameCount(int n){
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
