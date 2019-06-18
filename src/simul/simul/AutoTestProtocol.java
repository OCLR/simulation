package simul.simul;

import simul.infrastructure.MbusNetwork;
import simul.protocol.SimulationConfiguration;
import simul.protocol.Stats;

import java.io.File;
import java.io.IOException;
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

    public static void main(String[] args) throws Exception {
        String file;
        float ber;
        long lasting;
        final File folder = new File("examples");
        ArrayList<String> list = AutoTestProtocol.listFilesForFolder(folder);
        // List of file.
        for (int i = 0; i < list.size(); i++) {
            AutoTestProtocol p1 = new AutoTestProtocol("examples/"+list.get(i), 0.001f,20,true);
            //AutoTestProtocol p2 = new AutoTestProtocol("examples/"+list.get(i), 0.01f,200);
            //AutoTestProtocol p3 = new AutoTestProtocol("examples/"+list.get(i), 0.01f,2000);
            //AutoTestProtocol p4 = new AutoTestProtocol("examples/"+list.get(i), 0.01f,20000);
            p1.run();
            //p2.run();
            //p3.run();
            //p4.run();
            System.out.println("P1:");
            p1.printResults();
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

    public void printResults() {
        this.network.printResults();
    }


}
