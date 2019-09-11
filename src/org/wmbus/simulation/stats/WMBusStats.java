package org.wmbus.simulation.stats;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.wmbus.simulation.ResultTable;

/**
 *
 * @author lvlz
 */
public class WMBusStats {
    /*
     * WMBusMaster device.
     */
    public double masterSentMessage = 0;
    public int masterSumPath = 0;
    public double masterUpdateLink = 0;
    public double masterTrasmissionSuccess = 0;
    public double masterTrasmissionFaultWithNoUpdate = 0;
    public double masterTrasmissionFaultWithUpdate = 0;
    /*
    * Generic device including master.
    * */
    public double deviceTotalRetrasmission = 0;
    public double deviceTotalRetrasmissionCommunication = 0;
    public double deviceTrasmissionCommunication = 0;
    public double deviceTrasmissionTimeoutCommunication = 0;
    public double deviceSuccessTrasmissionCommunication = 0;

    /*
        Packet
     */
    public long globalTrasmissionResponseBlockNumber = 0;
    public long globalTrasmissionResponsePayloadParityBitSum = 0;
    public long globalTrasmissionResponseHeaderSum = 0;
    public long globalTrasmissionResponsePayloadNoParityBitSum = 0;
    public long globalTrasmissionResponseSum = 0;
    public long globalResponseTrasmissionCommunication = 0;
    
    public long globalTrasmissionRequestBlockNumber = 0;
    public long globalTrasmissionRequestHeaderSum = 0;
    public long globalTrasmissionRequestPayloadParityBitSum = 0;
    public long globalTrasmissionRequestPayloadNoParityBitSum = 0;
    public long globalTrasmissionRequestSum = 0;
    public long globalRequestTrasmissionCommunication = 0;

    public long globalRequestSuccessCommunication = 0;
    public long globalRequestFailCommunication= 0;

    public long  globalResponseSuccessCommunication = 0;
    public long globalResponseFailCommunication = 0;

    public boolean globalConvergence = false;


    public ResultTable printResults(){

        ResultTable r = new ResultTable();
        /* NO way */
        if (this.masterSentMessage == 0) {
            return r;
        }
        /* WMBusMaster node */
       /* r.addRow("Master #messages:",this.masterSentMessage);
        r.addRow("Master fault with no update #messages:",this.masterTrasmissionFaultWithNoUpdate);
        r.addRow("Master fault with updates #messages:",this.masterTrasmissionFaultWithUpdate);
        r.addRow("Master average path length:",this.masterSumPath/this.masterSentMessage);
        r.addRow("Master update Link Average times:",this.masterUpdateLink/this.masterSentMessage);
        r.addRow("Master update Link times:",this.masterUpdateLink);

        /* Device to  Device packet
        r.addRow("Device-to-Device #trasmission:",this.deviceTrasmissionCommunication);
        r.addRow("Device-to-Device Request  #trasmission:",this.globalRequestTrasmissionCommunication);
        r.addRow("Device-to-Device Response #trasmission:",this.globalResponseTrasmissionCommunication);
        r.addRow("Device-to-Device #retrasmission:",this.deviceTotalRetrasmission);
        r.addRow("Device-to-Device Single communication #retrasmission :",this.deviceTotalRetrasmissionCommunication);

        r.addRow("Device-to-Device Packet fault:",this.deviceTrasmissionTimeoutCommunication);
        r.addRow("Device-to-Device Packet success:",this.deviceSuccessTrasmissionCommunication);
        */

        // Relative Measures.
        r.addRow("Convergence (0-yes,1-no):" ,(this.globalConvergence?0.0:1.0));
        r.addRow("RM: Master Fault %:" ,((this.masterTrasmissionFaultWithNoUpdate+this.masterTrasmissionFaultWithUpdate)/this.masterSentMessage)*100);
        r.addRow("RM: Master average path length:",this.masterSumPath/this.masterSentMessage);
        if (this.globalRequestTrasmissionCommunication==0){
            r.addRow("RM: Device-Device Response Success Probability :",0);
            r.addRow("RM: Device-Device Response Failed Probability :",0);
        }else{
            r.addRow("RM: Device-Device Request Success Probability :",((this.globalRequestSuccessCommunication+0.f)/this.globalRequestTrasmissionCommunication)*100);
            r.addRow("RM: Device-Device Request Failed Probability :",((this.globalRequestFailCommunication+0.f)/this.globalRequestTrasmissionCommunication)*100);
        }

        if (this.globalResponseTrasmissionCommunication==0){
            r.addRow("RM: Device-Device Response Success Probability :",0);
            r.addRow("RM: Device-Device Response Failed Probability :",0);
        }else{
            r.addRow("RM: Device-Device Response Success Probability :",((this.globalResponseSuccessCommunication+0.f)/this.globalResponseTrasmissionCommunication)*100);
            r.addRow("RM: Device-Device Response Failed Probability :",((this.globalResponseFailCommunication+0.f)/this.globalResponseTrasmissionCommunication)*100);
        }

        // Request packet
        r.addRow("RM: Request Packet size",this.globalRequestTrasmissionCommunication==0?0:this.globalTrasmissionRequestSum/this.globalRequestTrasmissionCommunication);
        r.addRow("RM: Request Header size",this.globalRequestTrasmissionCommunication==0?0:this.globalTrasmissionRequestHeaderSum/this.globalRequestTrasmissionCommunication);
        r.addRow("RM: Request Payload size (no-ecc):",this.globalRequestTrasmissionCommunication==0?0:this.globalTrasmissionRequestPayloadNoParityBitSum/this.globalRequestTrasmissionCommunication);
        r.addRow("RM: Request Payload size (ecc):",this.globalRequestTrasmissionCommunication==0?0:this.globalTrasmissionRequestPayloadParityBitSum/this.globalRequestTrasmissionCommunication);
        r.addRow("RM: Request Packet Block count:",this.globalRequestTrasmissionCommunication==0?0:this.globalTrasmissionRequestBlockNumber/this.globalRequestTrasmissionCommunication);
        // Response packet
        r.addRow("RM: Response Packet size",this.globalResponseTrasmissionCommunication==0?0:this.globalTrasmissionResponseSum/this.globalResponseTrasmissionCommunication);
        r.addRow("RM: Response Header size",this.globalResponseTrasmissionCommunication==0?0:this.globalTrasmissionResponseHeaderSum/this.globalResponseTrasmissionCommunication);
        r.addRow("RM: Response Payload size (no-ecc):",this.globalResponseTrasmissionCommunication==0?0:this.globalTrasmissionResponsePayloadNoParityBitSum/this.globalResponseTrasmissionCommunication);
        r.addRow("RM: Response Payload size (ecc):",this.globalResponseTrasmissionCommunication==0?0:this.globalTrasmissionResponsePayloadParityBitSum/this.globalResponseTrasmissionCommunication);
        r.addRow("RM: Response Packet Block count:",this.globalResponseTrasmissionCommunication==0?0:this.globalTrasmissionResponseBlockNumber/this.globalResponseTrasmissionCommunication);

        return r;
        /* think carefully as*/
        /* Request packet % */
        /*r.addRow("Request success %:",this.globalRequestTrasmissionCommunication==0?0:WMbusMessage.getSuccessProb(this.globalTrasmissionRequestSum/this.globalRequestTrasmissionCommunication,ber)*100);
        if (WMbusSimulationConfig.CONF_HAMMING) {
            r.addRow("Request recoverable %",this.globalRequestTrasmissionCommunication==0?0:WMbusMessage.getRecoverableProb(globalTrasmissionRequestSum/this.globalRequestTrasmissionCommunication,ber)*100);
        }else {
            r.addRow("Request recoverable %",0);
        }
        r.addRow("Request fail %",this.globalRequestTrasmissionCommunication==0?0:WMbusMessage.getFaultProb(this.globalTrasmissionRequestSum/this.globalRequestTrasmissionCommunication,ber)*100);

        /*  -- Response packet %
        r.addRow("Response success %:",this.globalResponseTrasmissionCommunication==0?0:WMbusMessage.getSuccessProb(this.globalTrasmissionResponseSum/this.globalResponseTrasmissionCommunication,ber)*100);
        if (WMbusSimulationConfig.CONF_HAMMING) {
            r.addRow("Response recoverable %",this.globalResponseTrasmissionCommunication==0?0:WMbusMessage.getRecoverableProb(this.globalTrasmissionResponseSum/this.globalResponseTrasmissionCommunication,ber)*100);
        }else {
            r.addRow("Response recoverable %",0);
        }
        r.addRow("Response fail %",this.globalResponseTrasmissionCommunication==0?0:WMbusMessage.getFaultProb(this.globalTrasmissionResponseSum/this.globalResponseTrasmissionCommunication,ber)*100);
        return r;

         */
    }

    
}
