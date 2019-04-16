package simul.protocol;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author lvlz
 */
public class Stats {
    public static long masterSentMessage = 0;
    public static long masterAvgLength = 0;
    
    public static double avgBandwidth = 0;
    public static double avgBestBandwidth = 0;
    public static long fault = 0;
    public static long cacheHit = 0;
    public static long currentThroughtput = 0;
    public static long masterCacheHit;
    public static long CommunicationhopCount = 0;
    public static long masterReceivedMessage;
    public static int hopCount;
    public static String statFile = "";
    public static int updateNoiseSlave;
    public static int updateMasterSlave;
    public static int windowFault;
    public static String statFile2;
    
    
    
    
    
}
