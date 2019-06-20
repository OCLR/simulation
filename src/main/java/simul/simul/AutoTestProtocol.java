package simul.simul;

import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.writers.FileWriter;
import simul.infrastructure.MbusNetwork;
import simul.protocol.SimulationConfiguration;
import simul.protocol.Stats;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by Federico Falconi on 05/07/2017.
 */

public class AutoTestProtocol {
    private MbusNetwork network;


    public AutoTestProtocol(String name, float ber, long lasting,boolean hamming) throws IOException {
        this.network = new MbusNetwork(name, ber, lasting);
        SimulationConfiguration.CONF_HAMMING = hamming;
    }

    public String description() {
        /*return "Questo modello descrive il protocollo per utility network " +
                "proposto dal professor Culmone. Quindi il routing si basa su " +
                "una sequenza di hop codificata nell'header del pacchetto.";*/
        return " This model describes a protocol for utility network, proposed by prof. Culmone." +
        		" Therefore the routing is based on a precomputed sequence encoded with the packet header from master";
    }


    public void run() throws Exception {
        this.network.run();
    }

    public static ArrayList<String> listFilesForFolder(final File folder) {
        ArrayList<String> list = new ArrayList<String>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                list.add(fileEntry.getName());
            }
        }
        return list;
    }
    private static int powerN(int base, int n) {
        int result = 1;
        for (int i = 0; i < n; i++) {
            result *= base;
        }
        return result;
    }
  private static float powerNFloat(float base, int n) {
       float result = 1;
        for (int i = 0; i < n; i++) {
            result *= base;
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        String file;
        float ber;
        long lasting;
        Configurator.currentConfig()
                .level(Level.OFF)
                .activate();
        final File folder = new File("examples");
        ArrayList<String> list = AutoTestProtocol.listFilesForFolder(folder);
        // List of file.
        AutoTestProtocol test;

        PrintStream fileOut = new PrintStream("solutions/20nodesFormatCustom.csv");
        System.setOut(fileOut);
        for (int i = 0; i < list.size(); i++) {
                for (int ri = 2; ri < 9; ri++) {
                System.out.println("R -> 10^-"+ri);

                System.out.println("With Hamming (8,5)");
                AutoTestProtocol.printHeader();
                for (int j = 0; j < 5; j++) {
                    /*Configurator.currentConfig()
                            .writer(new FileWriter("solutions/20nodesNoHammingFormatA"+j+".txt  ", true, false))
                            .level(Level.INFO)
                            .activate();*/

                    test = new AutoTestProtocol("examples/"+list.get(i), AutoTestProtocol.powerNFloat(0.1f,ri),2*AutoTestProtocol.powerN(10,j),true);
                    test.run();
                    System.out.print("P"+j+'\t');
                    test.printResults(1);

                }
                System.out.println("With Checksum");
                AutoTestProtocol.printHeader();
                for (int j = 0; j < 5; j++) {
                    /*Configurator.currentConfig()
                            .writer(new FileWriter("solutions/20nodesNoHammingFormatA"+j+".txt  ", true, false))
                            .level(Level.INFO)
                            .activate();*/

                    test = new AutoTestProtocol("examples/"+list.get(i), AutoTestProtocol.powerNFloat(0.1f,ri),2*AutoTestProtocol.powerN(10,j),false);
                    test.run();
                    System.out.print("P"+j+'\t');
                    test.printResults(1);

                }

            }
            //System.out.println("P2:");
            //p2.printResults();
            //System.out.println("P3:");
            //p3.printResults();
            //System.out.println("P4:");
            //p4.printResults();
        }



        PrintWriter out = null;

        //if (Stats.masterSentMessage != 0) {
        	/* System.out.println("The weight's average in the header is: " + (protocol.headerSum / protocol.messagesSent) +
                     "(frames for packet)");*/

          /*  System.out.println("Slaves number:" + protocol.network.getSlavesNum());
            System.out.println("Ber parameter:" + protocol.network.getBer());
            System.out.println("Master message sent:" + Stats.masterSentMessage);
            System.out.println("Master performance %:" + (Stats.masterReceivedMessage / (Stats.masterSentMessage + 0.0)) * 100);
            System.out.println("Network trasmission %:" + Stats.trasmissionCommunication);
            System.out.println("Network Retrasmission %:" + Stats.reTrasmissionCommunication);
            System.out.println("Network Fault %:" + Stats.faultTrasmissionCommunication);*/
            //System.out.println("Master Cache Hit:"+ Stats.masterCacheHit);
            //System.out.println("Master Cache Impact:"+ ((Stats.masterCacheHit/(Stats.masterSentMessage+0.0))*100));
            //System.out.println("Bandwidth Cache   case avg :"+(Stats.avgBandwidth /Stats.masterSentMessage));
            //System.out.println("Bandwidth Default case avg  :"+(Stats.avgBestBandwidth /Stats.masterSentMessage));
            //System.out.println("Comparison index (0 < x < 1) :"+(Stats.avgBandwidth /Stats.masterSentMessage)/(Stats.avgBestBandwidth /Stats.masterSentMessage));
            //double bandw = (Stats.avgBandwidth /Stats.masterSentMessage)/(Stats.avgBestBandwidth /Stats.masterSentMessage);
               /* try{
                    FileWriter fw = new FileWriter("results.txt", true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    out = new PrintWriter(bw);
                    out.print(slaveNum);
                    out.print(";");
                    out.print(powerNoiseNodePercentage);
                    out.print(";");
                    out.print(variability);
                    out.print(";");
                    out.print(noiseEdgesNodePercentage);
                    out.print(";");
                    out.print(mediumDegree);
                    out.print(";");
                    out.print(lasting);
                    out.print(";");
                    out.print(Stats.masterReceivedMessage);
                    out.print(";");
                    out.print((Stats.masterReceivedMessage/(Stats.masterSentMessage+0.0))*100);
                    out.print(";");
                    /*out.print(Stats.masterCacheHit);
                    out.print(";");
                    out.print((Stats.masterCacheHit/(Stats.masterSentMessage+0.0))*100);
                    out.print(";");
                    out.print((Stats.avgBandwidth /Stats.masterSentMessage));
                    out.print(";");
                    out.print((Stats.avgBestBandwidth /Stats.masterSentMessage));
                    out.print(";");
                    out.print(bandw*100);
                    out.print(";");
                    out.print(Stats.masterAvgLength/Stats.masterSentMessage);
                    out.println();
                    //more code
                } catch (IOException e) {
                    //exception handling left as an exercise for the reader
                    throw e;
                }
        	 
        }else{
        	System.out.println("No package has been sent from the system; Change the clock");
        }*/
            //out.close();
            //protocol.master.log.close();
            //protocol.master.log_fault.close();
            //.master.band_log.close();

    }

    private static void printHeader() {
        Stats.printHeader();
    }

    public void printResults(int mode) {
        this.network.printResults(mode);
    }


}
