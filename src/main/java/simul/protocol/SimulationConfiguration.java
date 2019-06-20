package simul.protocol;

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
