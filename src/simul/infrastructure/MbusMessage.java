package simul.infrastructure;

import java.util.Random;

/**
 * Created by Federico Falconi on 04/07/2017.
 */
public abstract class MbusMessage {
    private int length;
    //private int frameErrors[];
    private double errorRate;
    private int source;
    private int hop;
    private int throughtput;
    

    public MbusMessage(int length,Integer source) {
        this.length = length;
        this.source = source;
        this.hop = 1;
        this.throughtput = 0;
        //frameErrors = new int[length];
    }


    public MbusMessage(MbusMessage other) {
        this.length = other.length;
        //this.frameErrors = other.frameErrors;
    }
    
    public int getThroughput() {
        return throughtput;
    }
    
    public void incThroughput(int size) {
        this.throughtput++;
    }
    public int getHop() {
        return hop;
    }
    
    public void incHop() {
        this.hop++;
    }
    public void setHop(int hop) {
        this.hop =hop;
    }
    
    public int getSource() {
        return source;
    }
    
    public void setSource(int source) {
        this.source = source;
    }
    
    public int getLength() {
        return length;
    }
    
    public int getSize() {
        return Integer.SIZE+Double.SIZE; 
    }

  /*  public int getErrors(int frame) {
        return frameErrors[frame];
    }*/

    /**
     * Generate error mask.
     * @param errorRate
     */
    public void generateErrors(double errorRate) {
//        int errors;
//        Random randomGen = new Random();
//        double gen;
//        // An error can occur in any of this bit.
//        for (int i = 0; i < frameErrors.length; i++) {
//            errors = 0;
//
//            for (int j = 1; j < 11; j++) {
//                gen = randomGen.nextDouble();
//                if (gen <= errorRate) {
//                    errors++;
//                }
//            }
//            
//
//            frameErrors[i] = errors;
//        }
        this.errorRate = errorRate;
    }

    public double getErrorRate() {
        return this.errorRate;
    }

}
