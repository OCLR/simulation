package simul.simul;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Random;

import simul.infrastructure.MbusNetwork;
import simul.protocol.Stats;

import java.util.Scanner;

/**
 * Created by Federico Falconi on 05/07/2017.
 */

public class ProtocolNetwork {
    private MbusNetwork network;
    

    public ProtocolNetwork( String name, float ber, long lasting) throws IOException {
        this.network = new MbusNetwork(name, ber, lasting);
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

    public static void main(String[] args) throws Exception {
        String file;
        float ber;
        long lasting;
        int packetDestinationMax;
        Scanner in = new Scanner(System.in);
        if (args.length == 3) {
            file = args[0];
            ber = Float.parseFloat(args[1]);
            lasting = Integer.parseInt(args[2]);
            //packetDestinationMax = Integer.parseInt(args[4]);

        } else {
            System.out.println("Insert the graph filename");
            file = in.nextLine();
            System.out.println("Insert the ber probability");
            ber = in.nextFloat();
            System.out.println("How many packets will sends?");
            lasting = in.nextLong();
            /*System.out.println("What is the noise range in a node? <= max arches.");
            noiseRange = in.nextInt();*/
        }

        Random r = new Random();
        // Stats.statFile = "distanceNoise-"+slaveNum+"-"+powerNoiseNodePercentage+"-"+variability+"-"+noiseEdgesNodePercentage+"-"+mediumDegree+"-"+lasting+".txt";
        // Stats.statFile2 = "fault-"+slaveNum+"-"+powerNoiseNodePercentage+"-"+variability+"-"+noiseEdgesNodePercentage+"-"+mediumDegree+"-"+lasting+".txt";

       /* Stats.updateMasterSlave = 0;
        Stats.updateNoiseSlave = 0;
        Stats.windowFault = 0;*/
        /*
        System.out.println("How many slaves ?");
        slaveNum = in.nextInt();
        System.out.println("Which is the noise average?");
        mediumNoise = in.nextDouble();
        System.out.println("Which is the probability that the noise change in a specific time?" );
        variability = in.nextInt();
        System.out.println("How many max arches incedence in a node?" );
        mediumDegree = in.nextInt();*/
        /*System.out.println("What is the noise range in a node? <= max arches.");
        noiseRange = in.nextInt();*/
        /*if (noiseRange>mediumDegree){
        	throw new IllegalArgumentException(" Noise range > max arches.");
        }*/
        /*System.out.println("How many nodes can be used as a packet destination?");
         packetDestinationMax = in.nextInt();
        System.out.println("How many packets will sends?" );
        lasting = in.nextLong();*/

        ProtocolNetwork protocol = new ProtocolNetwork(file, ber, lasting);
        protocol.run();

        
        PrintWriter out = null;
        //if (Stats.masterSentMessage != 0) {
        	/* System.out.println("The weight's average in the header is: " + (protocol.headerSum / protocol.messagesSent) +
                     "(frames for packet)");*/
          /*  System.out.println("Simulation results:");
            System.out.println("Network parameters:");
            System.out.println("Slaves number:" + protocol.network.getSlavesNum());
            System.out.println("Ber parameter:" + protocol.network.getBer());
            System.out.println("Master message sent:" + Stats.masterSentMessage);
            System.out.println("Master message received:" + Stats.masterReceivedMessage);
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
       // }
    }

}
