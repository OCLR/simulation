package org.wmbus.protocol.infrastructure;

import com.sun.jdi.InternalException;
import org.gfsk.GFSKModulation;
import org.wmbus.protocol.config.UniversalConstant;
import org.wmbus.protocol.config.WMBusConstant;
import org.wmbus.protocol.config.WMBusDeviceConfig;
import org.wmbus.simulation.WMBusSimulation;

/**
 * We assume additive white guassian noise for a specific frequency.
 */
public class WMBusNoise {
    private WMBusSimulation simulation;

    public WMBusNoise(WMBusSimulation simulation) {
        this.simulation = simulation;
    }

    public double getNoisePower(){
        double noiseVolt = simulation.getwMbusSimulationConfig().CONF_RANDOM.nextGaussian() * Math.sqrt(WMBusConstant.WMBUS_FREQUENCY_MAXNOISE_mW) ;
        // Assuming ohm = 1
        return noiseVolt*noiseVolt;
    }

    public double getBerFromDistance(double distance){
        double receivedPowerCostant = UniversalConstant.SPEED_OF_LIGHT /(4* Math.PI*distance*WMBusConstant.WMBUS_FREQUENCY);
        double antennaValue = WMBusDeviceConfig.CONF_ANTENNA_REAL_GAIN*WMBusDeviceConfig.CONF_ANTENNA_REAL_GAIN; // Same antenna receiver and sender.
        double receiverPower = WMBusDeviceConfig.CONF_TRASMITTER_POWER_LEVEL*(antennaValue*(receivedPowerCostant*receivedPowerCostant));
        // Check received power.
        if (receiverPower < WMBusConstant.WMBUS_FREQUENCY_MAXNOISE_mW){
            throw new InternalException("Received power "+receiverPower+"is a noise.");
        }


        /*
        double noise = this.simulation.getWMBusNoise().getNoisePower();
        double signalToNoiseRatio = (receiverPower/ noise );
        if (signalToNoiseRatio < UniversalConstant.DB_2 || signalToNoiseRatio > UniversalConstant.DB_20){
            throw new InternalException("Signal to Noise Ratio  Exceed limit: Distance:"+distance+" signalToNoiseRatio:"+signalToNoiseRatio+"  Received Power:"+receiverPower+" Noise:"+noise);
        }*/
        /**
         * 40dBm -> 100 meters.
         * 63,59 -> 1500 meters.
             * Mapping the range 40..63,59
         *                   20..1
         */
        /*
            Second analysis:

            Working only with SNR.
            SNR At the source is 50db ( a value, can be even higher) .
            SNR(receiver) = SNR(trasmitter) + a - 20*log(10)(distance)
            where a is :
            10*log((299792458÷(4×3,14×169×10^6))^2)
            log uses as a base 10.
            This means:

            SS      d   RS
            50	    4	20,9588001734407
            50	    7	16,0980391997149
            50	    10	13
            50	    13	10,7211329538633
            50	    16	8,9176003468815
            50	    19	7,42492798094342
            50	    22	6,15154638355588
            50	    25	5,04119982655925

            SS -> Source snr
            RS -> Destination snr.

            Final formula:
            SNR(receiver) = 50 - 17 - 20*log(d)
            log uses as a base 10.

             Maximum distance: 16 meters (10^-2 ber)


         */
        // v1.0.0 approach.
        //double distanceDb = (10 * Math.log10(distance*distance) );
        //double signalToNoiseRatioDb = WMBusNoise.scaleTo(distanceDb,63.59,40,20,1);
        double signalToNoiseRatioDb = 50 - 17 - 20*Math.log10(distance);
        // Logger.info(signalToNoiseRatioDb);
        double ber = GFSKModulation.computeBer(signalToNoiseRatioDb, WMBusConstant.WMBUS_GFSK_MODULATION_INDEX);
        //Logger.info(ber+" "+signalToNoiseRatioDb);
        return ber;
    }
    private static double scaleTo(double value, double OldMax, double OldMin, double NewMax, double NewMin ){
        double OldRange = (OldMax - OldMin);
        double  NewRange = (NewMax - NewMin);
        return  (((value - OldMin) * NewRange) / OldRange) + NewMin;
    }
}
