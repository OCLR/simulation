package org.wmbus.protocol.messages.formats;


import org.wmbus.protocol.config.WMBusDeviceConfig;
import org.wmbus.protocol.utilities.Hamming;
import org.wmbus.simulation.WMBusSimulation;

/**
 * Payload format
 * (7,4)
 *  4 bit data +  3 bit for hamming code.
 */
public abstract class MBusMessageFormatCustom extends MBusMessageFormatCore {
    private int preamble_size;

    public MBusMessageFormatCustom(WMBusSimulation simulation) {
        super(simulation);
        this.preamble_size = WMBusDeviceConfig.getHeaderSize(this.simulation.getwmbusDeviceConfig().CONF_PACKET_TRASMISSION);
    }
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

    public double computeECC(double ber){
        // int packetSize = this.getSize();
        // packetSize-=2;
        double hammingResultOneCount = 0;
        int size;
        int result = this.computeHamming(this.getBlockSize(0),ber);
        //if (result == 1){
        //hammingResultOneCount+=1;
        //}
        if (result > 1){
            return Integer.MAX_VALUE;
        }

        if (this.simulation.getwMbusSimulationConfig().CONF_HAMMING){
            // 4+3 4(payload) 3 header 1 codewords.
            size = this.getBlockSize(1);
        }else{
            // No hamming function
            // the rest.
            // -this.getMessageHeader() what? only the rest.
            size = this.getMessageSize();
        }

        for (int i = 1; i < this.getMessageBlockCount();i++){
            result = this.computeHamming(size,ber);
            if (result == 1){
                hammingResultOneCount+=1;
            }
            if (result > 1){
                return Integer.MAX_VALUE;
            }
        }
        if (simulation.getwMbusSimulationConfig().CONF_DETAILED_NOISE){
            return hammingResultOneCount/this.getMessageBlockCount();
        }else{
            return 0;
        }

    }

    public double computeECCSuccess(double ber){
        // int packetSize = this.getSize();
        // packetSize-=2;
        double success = 0;
        double hammingResultOneCount = 0;
        int size;
        success = Hamming.getSuccessProb(this.simulation,this.getBlockSize(0), ber);

        if (this.simulation.getwMbusSimulationConfig().CONF_HAMMING){
            size = this.getBlockSize(1);
        }else{
            // No hamming function
            size = this.getMessageSize()-this.getMessageHeader();
        }



        for (int i = 1; i < this.getMessageBlockCount();i++){
            success *= Hamming.getSuccessProb(this.simulation,size,ber);
        }
        return success;
    }

    public double computeECCFail(double ber){
        // int packetSize = this.getSize();
        // packetSize-=2;
        double fail = 0;
        double hammingResultOneCount = 0;
        int size;
        fail = Hamming.getFaultProb(this.simulation,this.getBlockSize(0), ber);

        if (this.simulation.getwMbusSimulationConfig().CONF_HAMMING){
            size = this.getBlockSize(1);
        }else{
            // No hamming function
            size = this.getMessageSize()-this.getMessageHeader();
        }

        for (int i = 1; i < this.getMessageBlockCount();i++){
            fail += Hamming.getFaultProb(this.simulation,size,ber);
        }
        return fail;
    }

    public double computeECCRecoverable(double ber){
        // int packetSize = this.getSize();
        // packetSize-=2;
        double rec = 0;
        double hammingResultOneCount = 0;
        int size;
        // success = Hamming.getSuccessProb(this.simulation,this.getBlockSize(0), ber);

        if (this.simulation.getwMbusSimulationConfig().CONF_HAMMING){
            size = this.getBlockSize(1);
        }else{
            // No hamming function
            size = this.getMessageSize()-this.getMessageHeader();
        }



        for (int i = 1; i < this.getMessageBlockCount();i++){
            rec += Hamming.getRecoverableProb(this.simulation,size,ber);
        }

        return rec;
    }
    public int computeFullFrameSize(int n){
        /* A block for header. */
        int headerBlockSize = this.getBlockSize(0);
        int dataBlockSize = this.getBlockSize(1);
        if (!this.simulation.getwMbusSimulationConfig().CONF_HAMMING){
            return (n); // Simply data
        }
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
        if (!this.simulation.getwMbusSimulationConfig().CONF_HAMMING){
            return (n); // Simply data.
        }
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
        if (!this.simulation.getwMbusSimulationConfig().CONF_HAMMING){
            return n;// return (n-headerBlockSize)+4; // 4 byte of CRC.
        }
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
        if (!this.simulation.getwMbusSimulationConfig().CONF_HAMMING){
            return 2;//return (n-headerBlockSize)+4; // 4 byte of CRC.
        }
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
