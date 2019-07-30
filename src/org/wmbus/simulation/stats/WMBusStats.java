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
    public long masterSentMessage = 0;
    public long masterSumPath = 0;
    public long masterUpdateLink = 0;
    public long masterTrasmissionSuccess = 0;
    public long masterTrasmissionFaultWithNoUpdate = 0;
    public long masterTrasmissionFaultWithUpdate = 0;
    /*
    * Generic device including master.
    * */
    public long deviceRetrasmissionCommunication = 0;
    public long deviceTrasmissionCommunication = 0;
    public long deviceTrasmissionTimeoutCommunication = 0;
    public long deviceSuccessTrasmissionCommunication = 0;

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
    public double globalRequestProbSuccess = 0;
    public double globalRequestProbFail = 0;
    public double globalRequestProbRecoverable = 0;
    public double globalResponseProbSuccess = 0;
    public double globalResponseProbFail = 0;
    public double globalResponseProbRecoverable = 0;


    public ResultTable printResults(){

        ResultTable r = new ResultTable();
        /* NO way */
        if (this.masterSentMessage == 0) {
            return r;
        }
        /* WMBusMaster node */
        r.addRow("Master #messages:",this.masterSentMessage);
        r.addRow("Master fault with no update #messages:",this.masterTrasmissionFaultWithNoUpdate);
        r.addRow("Master fault with updates #messages:",this.masterTrasmissionFaultWithUpdate);
        r.addRow("Master average path length:",this.masterSumPath/this.masterSentMessage);
        r.addRow("Master update Link Average times:",this.masterUpdateLink/this.masterSentMessage);
        r.addRow("Master update Link times:",this.masterUpdateLink);

        /* Device to  Device packet*/
        r.addRow("Device-to-Device #trasmission:",this.deviceTrasmissionCommunication);
        r.addRow("Device-to-Device Request  #trasmission:",this.globalRequestTrasmissionCommunication);
        r.addRow("Device-to-Device Response #trasmission:",this.globalResponseTrasmissionCommunication);
        r.addRow("Device-to-Device Packet retrasmission:",this.deviceRetrasmissionCommunication);
        r.addRow("Device-to-Device Packet fault:",this.deviceTrasmissionTimeoutCommunication);
        r.addRow("Device-to-Device Packet success:",this.deviceSuccessTrasmissionCommunication);
        /* Request packet */
        r.addRow("Request Packet size",this.globalRequestTrasmissionCommunication==0?0:this.globalTrasmissionRequestSum/this.globalRequestTrasmissionCommunication);
        r.addRow("Request Header size",this.globalRequestTrasmissionCommunication==0?0:this.globalTrasmissionRequestHeaderSum/this.globalRequestTrasmissionCommunication);
        r.addRow("Request Payload size (no-ecc):",this.globalRequestTrasmissionCommunication==0?0:this.globalTrasmissionRequestPayloadNoParityBitSum/this.globalRequestTrasmissionCommunication);
        r.addRow("Request Payload size (ecc):",this.globalRequestTrasmissionCommunication==0?0:this.globalTrasmissionRequestPayloadParityBitSum/this.globalRequestTrasmissionCommunication);
        r.addRow("Request Packet Block count:",this.globalRequestTrasmissionCommunication==0?0:this.globalTrasmissionRequestBlockNumber/this.globalRequestTrasmissionCommunication);
        /* Response packet */
        r.addRow("Response Packet size",this.globalResponseTrasmissionCommunication==0?0:this.globalTrasmissionResponseSum/this.globalResponseTrasmissionCommunication);
        r.addRow("Response Header size",this.globalResponseTrasmissionCommunication==0?0:this.globalTrasmissionResponseHeaderSum/this.globalResponseTrasmissionCommunication);
        r.addRow("Response Payload size (no-ecc):",this.globalResponseTrasmissionCommunication==0?0:this.globalTrasmissionResponsePayloadNoParityBitSum/this.globalResponseTrasmissionCommunication);
        r.addRow("Response Payload size (ecc):",this.globalResponseTrasmissionCommunication==0?0:this.globalTrasmissionResponsePayloadParityBitSum/this.globalResponseTrasmissionCommunication);
        r.addRow("Response Packet Block count:",this.globalResponseTrasmissionCommunication==0?0:this.globalTrasmissionResponseBlockNumber/this.globalResponseTrasmissionCommunication);

        r.addRow("Request success %",this.globalRequestTrasmissionCommunication==0?0:(this.globalRequestProbSuccess /this.globalRequestTrasmissionCommunication)*100);
        r.addRow("Request recoverable %",this.globalRequestTrasmissionCommunication==0?0:(this.globalRequestProbRecoverable /this.globalRequestTrasmissionCommunication)*100);
        r.addRow("Request fail %",this.globalRequestTrasmissionCommunication==0?0:(this.globalRequestProbFail /this.globalRequestTrasmissionCommunication)*100);

        r.addRow("Response success %:",this.globalResponseTrasmissionCommunication==0?0:(this.globalResponseProbSuccess /this.globalRequestTrasmissionCommunication)*100);
        r.addRow("Response recoverable %:",this.globalResponseTrasmissionCommunication==0?0:(this.globalResponseProbRecoverable /this.globalRequestTrasmissionCommunication)*100);
        r.addRow("Response fail %:",this.globalResponseTrasmissionCommunication==0?0:(this.globalResponseProbFail /this.globalRequestTrasmissionCommunication)*100);

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
