package org.wmbus.protocol.config;

public class WMBusMasterConfig {
    /* All possible configuration values */
    public static final int DESTINATION_FETCH_SEQUENCE = 0;
    public static final int DESTINATION_FETCH_RANDOM = 1;
    /*The current configuration*/

    public static final int CONF_DESTINATION_FETCH= WMBusMasterConfig.DESTINATION_FETCH_SEQUENCE;
    public static final Integer BACKOFF_ATTEMPT_START = 5;
    public static final Integer BACKOFF_ATTEMPT_MAX = 5;
}
