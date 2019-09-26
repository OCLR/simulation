package org.wmbus.protocol.infrastructure;

import org.apache.commons.math3.special.Erf;
import org.pmw.tinylog.Logger;
import org.wmbus.protocol.config.WMBusConstant;
import org.wmbus.simulation.WMBusSimulation;

/**
 * We assume additive white guassian noise for a specific frequency.
 */
public class WMBusNoise {
    private WMBusSimulation simulation;

    public WMBusNoise(WMBusSimulation simulation) {
        this.simulation = simulation;
    }

    public double getNoisePower( double noiseMWVariance){
        double noiseVolt = simulation.getwMbusSimulationConfig().CONF_RANDOM.nextGaussian() * Math.sqrt(noiseMWVariance) ;
        // Assuming ohm = 1
        return noiseVolt*noiseVolt; //  *noiseVoltAvoid negative part.
    }

    public double getBerFromDistance(double distance) throws Exception {
        double noiseVarianceMW = Math.pow(10, this.simulation.getwmbusDeviceConfig().WMBUS_FREQUENCY_NOISE_DBM/10);
        // double signalPowerMw = Math.pow(10, this.simulation.getwmbusDeviceConfig().CONF_TRASMITTER_POWER_LEVEL_DBM/10);
        double antennaGain = Math.pow(10,this.simulation.getwmbusDeviceConfig().CONF_ANTENNA_GAIN_DB/10)*2; // Same for receiver and trasmitter.
        // Compute noise level.
        double noiseDBActual = 0;
        double noiseMwActual = 0 ;
        do {
            noiseMwActual = this.getNoisePower(noiseVarianceMW);
            noiseDBActual = (10 * Math.log10(noiseMwActual));
        }while (noiseDBActual > -70);
        double  pathlossDb  =20*Math.log10(distance)+20*Math.log10(WMBusConstant.WMBUS_FREQUENCY_MHZ)-27.55-antennaGain;
        double  receiverPowerDb = this.simulation.getwmbusDeviceConfig().CONF_TRASMITTER_POWER_LEVEL_DBM - pathlossDb;
        double  SignalToNoiseRatio = receiverPowerDb - noiseDBActual;
        double  ber = 0.5* Erf.erfc(Math.sqrt(SignalToNoiseRatio/2));
        Logger.trace("DISTANCE: "+distance + " NOISE DB:" + noiseDBActual + " PATH LOSS DB:" + pathlossDb + " POWER REC DB:" + receiverPowerDb + "\n SNR DB:" + SignalToNoiseRatio + " BER: "+ber);
        if (Double.isNaN(ber)) {
            Logger.error("Ber Cannot be Nan, COMMUNICATIONS CANNOT OCCURS WITH THIS LEVEL OF NOISE."+ noiseDBActual+ " dbm. MESSAGE ERROR ");
            throw new Exception("DISTANCE: "+distance + " NOISE DB:" + noiseDBActual + " PATH LOSS DB:" + pathlossDb + " POWER REC DB:" + receiverPowerDb + "\n SNR DB:" + SignalToNoiseRatio + " BER: "+ber);
            // no cannot be.
        }
        return ber;
        /*
         Third analysis: Working with
            Noise dbm
            -70
            Distance

                5
                25
                50
                75
                100
                150
                175
                200
                225
                250
            Free space Ber(-70db)
                8,43076835058939E-11
                1,11610576968615E-07
                2,5488747961865E-06
                1,61419617445414E-05
                6,04176550678364E-05
                0,000396449845576
                0,000817975270038
                0,001540384912243
                0,002706596103981
                0,004505096455511
         */


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
        // double signalToNoiseRatioDb = 50 - 17 - 20*Math.log10(distance); GFSK
        // Logger.info(signalToNoiseRatioDb);
        // double ber = GFSKModulation.computeBer(signalToNoiseRatioDb, WMBusConstant.WMBUS_GFSK_MODULATION_INDEX); GFSK
        //Logger.info(ber+" "+signalToNoiseRatioDb);

    }
    private static double scaleTo(double value, double OldMax, double OldMin, double NewMax, double NewMin ){
        double OldRange = (OldMax - OldMin);
        double  NewRange = (NewMax - NewMin);
        return  (((value - OldMin) * NewRange) / OldRange) + NewMin;
    }
}
