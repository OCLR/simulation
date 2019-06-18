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

    public int globalTrasmissionFault = 0;
    public int globalTrasmissionSuccess = 0;
    
    public void printResults(float ber){
        System.out.println(
                "Master sent messages: "+this.masterSentMessage+'\n'+
                "Master average path: "+(this.masterSumPath/this.masterSentMessage)+'\n'+
                "Master average Update Link: " + this.masterAvgNumberOfUpdatedLink+'\n'+
                "Global retrasmission number: "+this.globalRetrasmissionCommunication+'\n'+
                "Global trasmission number:"+this.globalTrasmissionCommunication+'\n'+
                "Global number of fault: "+this.globalTrasmissionFault+'\n'+
                "Global number of success: "+this.globalTrasmissionSuccess+'\n'+
                "Global packet size (avg): "+this.globalTrasmissionPacketSum/this.globalTrasmissionCommunication+'\n'+
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
