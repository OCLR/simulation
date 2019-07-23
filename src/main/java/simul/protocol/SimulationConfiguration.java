package simul.protocol;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import smile.math.special.Erf;

import java.util.HashMap;
import java.util.Random;

public class SimulationConfiguration {
    /* All possible configuration values */
    public static final int DESTINATION_FETCH_SEQUENCE = 0;
    public static final int DESTINATION_FETCH_RANDOM = 1;
    public static final int TELEGRAM_FORMAT_A = 0;
    public static final int TELEGRAM_FORMAT_B = 1;
    public static final int TRASMISSION_MODE_S1= 0;
    public static final int TRASMISSION_MODE_S1M = 1;
    public static final int TRASMISSION_MODE_S2 = 2;
    public static final int TRASMISSION_MODE_T1 = 3;
    public static final int TRASMISSION_MODE_T2 = 4;
    public static final int TRASMISSION_MODE_R2 = 5;
    public static final int TRASMISSION_MODE_C1 = 6;
    public static final int TRASMISSION_MODE_C2 = 7;
    public static final int TRASMISSION_MODE_N1 = 8;
    public static final int TRASMISSION_MODE_N2 = 9;

    public static final byte PACKET_REQUEST = 0;
    public static final byte PACKET_REQUESTACK = 1;
    public static final byte PACKET_RESPONSE = 2;
    public static final byte PACKET_GENERIC = 3;

    /*The current configuration*/

    public static final int CONF_DESTINATION_FETCH= SimulationConfiguration.DESTINATION_FETCH_SEQUENCE;
    /* The mode changes the size in terms of chips and the conversion between chips and bits depends on the enconding which depends on mode. */
    public static final int CONF_PREHEADER = SimulationConfiguration.getHeaderSize(); // PREAMBLE + SYNC WORD. depending on the modality.
    public static final int CONF_PACKET_TYPE = SimulationConfiguration.TELEGRAM_FORMAT_A; // PREAMBLE + SYNC WORD. depending on the modality.
    public static final int CONF_PACKET_TRASMISSION = SimulationConfiguration.TRASMISSION_MODE_S2;
    public static final double CONF_TRASMITTER_POWER_LEVEL = 125.892541179; // Trasmitter power level. 21 dbm ( -27 to +27 dbm 3dbm step)
    public static final int CONF_MAX_DISTANCE = 1500; // Meters
    public static final double CONF_NOISE_POWER= SimulationConfiguration.getNoisePower(SimulationConfiguration.CONF_MAX_DISTANCE,SimulationConfiguration.CONF_TRASMITTER_POWER_LEVEL);
    public static final double[]  GFSK_SNR_BER_VALUES_SNR = {
            1.0,
            1.25892541179417,
            1.58489319246111,
            1.99526231496888,
            2.51188643150958,
            3.16227766016838,
            3.98107170553497,
            5.01187233627272,
            6.30957344480193,
            7.94328234724282,
            10.0,
            12.5892541179417,
            15.8489319246111,
            19.9526231496888,
            25.1188643150958,
            31.6227766016838,
            39.8107170553497,
            50.1187233627272,
            63.0957344480193,
            79.4328234724281,
            100.0
    };
    public static final double[]  GFSK_SNR_BER_VALUES_BER = {
            0.326404974528976,
            0.293454919679532,
            0.257224475059943,
            0.218628024903216,
            0.179044421136338,
            0.140240119248937,
            0.104162605776203,
            0.072621784979416,
            0.046939191529242,
            0.027683923635412,
            0.014591214501437,
            0.006686170994048,
            0.002570091310722,
            7.92E-04,
            1.85E-04,
            3.03E-05,
            3.20E-06,
            1.94E-07,
            5.85E-09,
            7.32E-11,
            3.03E-13,

    };
      /*    SNR(DB)     SNR                BER value.
            0.0         1.0           0.3264049745289765
            1.0  1.2589254117941673   0.29345491967953247
            2.0  1.5848931924611136   0.2572244750599429
            3.0  1.9952623149688795   0.21862802490321576
            4.0  2.51188643150958     0.17904442113633823
            5.0  3.1622776601683795   0.14024011924893728
            6.0  3.9810717055349722   0.1041626057762035
            7.0  5.011872336272722    0.07262178497941585
            8.0  6.309573444801933    0.04693919152924151
            9.0   7.943282347242816   0.02768392363541227
            10.0 10.0                0.014591214501437398
            11.0 12.589254117941675  0.006686170994047898
            12.0 15.848931924611133  0.0025700913107220306
            13.0 19.952623149688797  7.917759990529756E-4
            14.0 25.118864315095795  1.8459164003864435E-4
            15.0 31.622776601683793  3.030373025648944E-5
            16.0 39.810717055349734  3.2000137349043685E-6
            17.0 50.11872336272722   1.9396850150578905E-7
            18.0 63.09573444801933   5.84771643791071E-9
            19.0 79.43282347242814   7.320504719671854E-11
            20.0 100.0               3.0318656203352484E-13
       */

    private static double getNoisePower(double distance, double trasmitterPowerLevel) {

        SplineInterpolator interpolatorGenerator =new SplineInterpolator();
        PolynomialSplineFunction sf = interpolatorGenerator.interpolate(SimulationConfiguration.GFSK_SNR_BER_VALUES_BER,SimulationConfiguration.GFSK_SNR_BER_VALUES_SNR);
        double SNR = sf.value(Math.pow(10,-8));
        // We assume maximum distance so.
        double receivedPower = trasmitterPowerLevel * (1/(distance*distance));
        /* SNR -> receivedPower/Noise */
        /* SNR/receivedPower -> 1/Noise */
        /* receivedPower/SNR -> Noise */

        return receivedPower/SNR;
    }


    public static final Random CONF_RANDOM = new Random();
    public static final int CONF_NUMBER_OF_RETRASMISSION = 2;
    public static boolean CONF_HAMMING = false;




    public static int getHeaderSize(){
        int tras_mode = SimulationConfiguration.CONF_PACKET_TRASMISSION;

        /**
         * Encoding rules:
         * Manchester:
         * 1 bit represents two chips.
         * https://www.maximintegrated.com/en/app-notes/index.mvp/id/3435
         *
         *
         */
        switch (tras_mode){
            case TRASMISSION_MODE_S1:
                // Manchester encoding
                // 582 chips.
                // 291 bit
                // Using byte encoding: 288 bit/8 -> 36
                // Using byte encoding: 296 bit/8 -> 37
                // byte?!? 36,375
                return 37;// ((582/2)/8)

            case TRASMISSION_MODE_S1M:
                // Manchester encoding
                // 56 chips.
                // 28 bit
                // Using byte encoding: 24 bit/8 -> 3
                // Using byte encoding: 32 bit/8 -> 4
                // byte?!? 3,5
                return 4;// ((582/2)/8)

            case TRASMISSION_MODE_S2:
                // Manchester encoding
                // 56 chips.
                // 28 bit
                // Using byte encoding: 24 bit/8 -> 3
                // Using byte encoding: 32 bit/8 -> 4
                // byte?!? 3,5
                return 4;// ((582/2)/8)

            case TRASMISSION_MODE_T1:
                // 3 out of 6 encoding
                // 56 chip
                // how many bit?!? dunno know
                return 4;

            case TRASMISSION_MODE_T2:
                // Manchester or 3outof6.
                // 56 chip
                // 28 bit
                // Using byte encoding: 24 bit/8 -> 3
                // Using byte encoding: 32 bit/8 -> 4
                // byte?!? 3,5
                return 4;
                //break;
            case TRASMISSION_MODE_R2:
                // Manchester or 3outof6.
                // 104 chip
                // 52 bit
                // Using byte encoding: 48 bit/8 -> 6
                // Using byte encoding: 56 bit/8 -> 7
                // byte?!? 6,5
                return 7;
               // break;
            case TRASMISSION_MODE_C1:
                // NRZ
                // 64 chip
                // 64 bit
                // Using byte encoding: 64 bit/8 -> 8
                return 8;
                //break;
            case TRASMISSION_MODE_C2:
                // NRZ
                // 64 chip
                // 64 bit
                // Using byte encoding: 64 bit/8 -> 8
                return 8;
            default:
                return 0;
        }
    }
}
