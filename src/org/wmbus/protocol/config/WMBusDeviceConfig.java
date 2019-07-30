package org.wmbus.protocol.config;

public class WMBusDeviceConfig {
    public  final static double CONF_TRASMITTER_POWER_LEVEL = 500; // Trasmitter power level. 21 dbm(125.892541179) ( -27 to +27(500mW) dbm 3dbm step)
    public  final static int CONF_PREHEADER = WMBusDeviceConfig.getHeaderSize(WMBusDeviceConfig.CONF_PACKET_TRASMISSION); // PREAMBLE + SYNC WORD. depending on the modality.
    public  final static int CONF_PACKET_TYPE = WMBusConstant.TELEGRAM_FORMAT_A; // PREAMBLE + SYNC WORD. depending on the modality.
    public  final static int CONF_PACKET_TRASMISSION = WMBusConstant.TRASMISSION_MODE_S2;
    public  final static int CONF_NUMBER_OF_RETRASMISSION = 2;
    public static final double CONF_ANTENNA_GAIN_DB = 0;
    public static final double CONF_ANTENNA_REAL_GAIN = Math.pow(10,WMBusDeviceConfig.CONF_ANTENNA_GAIN_DB/10)*1.64;

    public  static int getHeaderSize(int trans_mode){

        /**
         * Encoding rules:
         * Manchester:
         * 1 bit represents two chips.
         * https://www.maximintegrated.com/en/app-notes/index.mvp/id/3435
         *
         *
         */
        switch (trans_mode){
            case WMBusConstant.TRASMISSION_MODE_S1:
                // Manchester encoding
                // 582 chips.
                // 291 bit
                // Using byte encoding: 288 bit/8 -> 36
                // Using byte encoding: 296 bit/8 -> 37
                // byte?!? 36,375
                return 37;// ((582/2)/8)

            case WMBusConstant.TRASMISSION_MODE_S1M:
                // Manchester encoding
                // 56 chips.
                // 28 bit
                // Using byte encoding: 24 bit/8 -> 3
                // Using byte encoding: 32 bit/8 -> 4
                // byte?!? 3,5
                return 4;// ((582/2)/8)

            case WMBusConstant.TRASMISSION_MODE_S2:
                // Manchester encoding
                // 56 chips.
                // 28 bit
                // Using byte encoding: 24 bit/8 -> 3
                // Using byte encoding: 32 bit/8 -> 4
                // byte?!? 3,5
                return 4;// ((582/2)/8)

            case WMBusConstant.TRASMISSION_MODE_T1:
                // 3 out of 6 encoding
                // 56 chip
                // how many bit?!? dunno know
                return 4;

            case WMBusConstant.TRASMISSION_MODE_T2:
                // Manchester or 3outof6.
                // 56 chip
                // 28 bit
                // Using byte encoding: 24 bit/8 -> 3
                // Using byte encoding: 32 bit/8 -> 4
                // byte?!? 3,5
                return 4;
            //break;
            case WMBusConstant.TRASMISSION_MODE_R2:
                // Manchester or 3outof6.
                // 104 chip
                // 52 bit
                // Using byte encoding: 48 bit/8 -> 6
                // Using byte encoding: 56 bit/8 -> 7
                // byte?!? 6,5
                return 7;
            // break;
            case WMBusConstant.TRASMISSION_MODE_C1:
                // NRZ
                // 64 chip
                // 64 bit
                // Using byte encoding: 64 bit/8 -> 8
                return 8;
            //break;
            case WMBusConstant.TRASMISSION_MODE_C2:
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
