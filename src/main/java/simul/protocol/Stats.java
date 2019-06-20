package simul.protocol;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import simul.messages.MbusMessage;
import simul.simul.AutoTestProtocol;

/**
 *
 * @author lvlz
 */
public class Stats {
    public long masterSentMessage = 0;
    public long masterSumPath = 0;
    public long masterAvgNumberOfUpdatedLink = 0;

    public int globalRetrasmissionCommunication = 0;
    public int globalTrasmissionCommunication = 0;
    public int globalTrasmissionPacketSum = 0;
    public int globalTrasmissionBlockNumber = 0;
    public int globalTrasmissionPayloadParityBitSum = 0;
    public int globalTrasmissionPayloadNoParityBitSum = 0;
    public int globalTrasmissionHeaderSum = 0; // header


    public int globalTrasmissionFault = 0;
    public int globalTrasmissionSuccess = 0;

    public static void printHeader() {
        System.out.println(
                "Process" +'\t'+
                        "Master sent messages" +'\t'+
                        "Master average path"+'\t'+
                        "Master average Update Link"+'\t'+
                        "Global retrasmission number"+'\t'+
                        "Global trasmission number"+'\t'+
                        "Global number of fault"+'\t'+
                        "Global number of success" +'\t'+
                        "Global Full packet size with parity bit  (avg)" +'\t'+
                        "Global Payload with parity bit (avg)" +'\t'+
                        "Global Payload without parity bit (avg)" +'\t'+
                        "Global Header bit (avg)" +'\t'+
                        "Global packet block count (avg)"+'\t'+
                        "Global packet success rate%"+'\t'+
                        "Global Packet recoverable rate%"+'\t'+
                        "Global Packet fail rate%"
        );


    }

    public void printResults(float ber,int mode){
        if (mode == 0){
            System.out.println(
                    "Master sent messages: "+this.masterSentMessage+'\n'+
                    "Master average path: "+(this.masterSumPath/this.masterSentMessage)+'\n'+
                    "Master average Update Link: " + this.masterAvgNumberOfUpdatedLink+'\n'+
                    "Global retrasmission number: "+this.globalRetrasmissionCommunication+'\n'+
                    "Global trasmission number:"+this.globalTrasmissionCommunication+'\n'+
                    "Global number of fault: "+this.globalTrasmissionFault+'\n'+
                    "Global number of success: "+this.globalTrasmissionSuccess+'\n'+
                    "Global Full packet size with parity bit (avg): "+this.globalTrasmissionPacketSum/this.globalTrasmissionCommunication+'\n'+
                    "Global Payload with parity bit (avg): "+((SimulationConfiguration.CONF_HAMMING)?this.globalTrasmissionPayloadParityBitSum/this.globalTrasmissionCommunication:"-")+'\n'+
                    "Global Payload without parity bit (avg): "+this.globalTrasmissionPayloadNoParityBitSum/this.globalTrasmissionCommunication+'\n'+
                    "Global Header sum (avg): "+this.globalTrasmissionHeaderSum/this.globalTrasmissionCommunication+'\n'+
                    "Global packet block count (avg): "+this.globalTrasmissionBlockNumber/this.globalTrasmissionCommunication
            );

            if (SimulationConfiguration.CONF_HAMMING){
                System.out.println(
                        "Global Packet success rate%: "+ MbusMessage.getSuccessProb(this.globalTrasmissionPacketSum/this.globalTrasmissionCommunication,ber)*100+'\n'+
                        "Global Packet recoverable rate%: "+MbusMessage.getRecoverableProb(this.globalTrasmissionPacketSum/this.globalTrasmissionCommunication,ber)*100+'\n'+
                        "Global Packet fail rate%: "+ MbusMessage.getFaultProb(this.globalTrasmissionPacketSum/this.globalTrasmissionCommunication,ber)*100
                );
            }else{
                System.out.println(
                    "Global Packet Success rate: "+MbusMessage.getSuccessProb(this.globalTrasmissionPacketSum/this.globalTrasmissionCommunication,ber)*100+'\n'+
                    "Global Packet fail rate: "+MbusMessage.getFaultProb(this.globalTrasmissionPacketSum/this.globalTrasmissionCommunication,ber)*100
                );
            }
        }else if (mode == 1){
            System.out.print(
                    ""+this.masterSentMessage+'\t'+
                    (this.masterSumPath/this.masterSentMessage)+'\t'+
                            this.masterAvgNumberOfUpdatedLink+'\t'+
                            this.globalRetrasmissionCommunication+'\t'+
                            this.globalTrasmissionCommunication+'\t'+
                            this.globalTrasmissionFault+'\t'+
                            this.globalTrasmissionSuccess+'\t'+
                            (this.globalTrasmissionPacketSum/this.globalTrasmissionCommunication)+'\t'+
                            ((SimulationConfiguration.CONF_HAMMING)?this.globalTrasmissionPayloadParityBitSum/this.globalTrasmissionCommunication:"-")+'\t'+
                            this.globalTrasmissionPayloadNoParityBitSum/this.globalTrasmissionCommunication+'\t'+
                            this.globalTrasmissionHeaderSum/this.globalTrasmissionCommunication+'\t'+
                            this.globalTrasmissionBlockNumber/this.globalTrasmissionCommunication+'\t'
            );

            if (SimulationConfiguration.CONF_HAMMING){
                System.out.println(
                        ""+MbusMessage.getSuccessProb(this.globalTrasmissionPacketSum/this.globalTrasmissionCommunication,ber)*100+'\t'+
                         MbusMessage.getRecoverableProb(this.globalTrasmissionPacketSum/this.globalTrasmissionCommunication,ber)*100+'\t'+
                         MbusMessage.getFaultProb(this.globalTrasmissionPacketSum/this.globalTrasmissionCommunication,ber)*100
                );
            }else{
                System.out.println(
                        ""+MbusMessage.getSuccessProb(this.globalTrasmissionPacketSum/this.globalTrasmissionCommunication,ber)*100 +'\t'+"-"+'\t'+
                        MbusMessage.getFaultProb(this.globalTrasmissionPacketSum/this.globalTrasmissionCommunication,ber)*100
                );
            }
        }

    }

    public void init(){
        this.masterSentMessage = 0;
        this.masterSumPath = 0;
        this.masterAvgNumberOfUpdatedLink = 0;
        this.globalRetrasmissionCommunication = 0;
        this.globalTrasmissionCommunication = 0;
        this.globalTrasmissionFault = 0;
        this.globalTrasmissionSuccess = 0;
    }
    
}
