package simul.base;

/**
 * Created by Federico Falconi on 29/06/2017.
 */


public class NetConfig {
    protected final int NODES;
    private double[][] adjacenceMatrix;

    NetConfig(int nodes) {
        int n1,n2;
        NODES = nodes;
        adjacenceMatrix = new double[NODES][NODES];
        // Fill initialy.
        for (n1 = 0; n1 < nodes;n1++){
            for (n2 = 0; n2 < nodes;n2++){
                double noise = 0;//NetConfigManager.updateSingleNoise();       
                //System.out.println(noise);
                adjacenceMatrix[n1][n2] = noise ; // init.
            }
        }
    }

    public void setEdge(int from, int to, double noise) {
        try
        {
            adjacenceMatrix[to][from] = noise;
            adjacenceMatrix[from][to] = noise;
        }
        catch (ArrayIndexOutOfBoundsException index)
        {
            
            System.out.println("The vertices does not exists");
        }
    }

    public double getEdge(int from, int to) {
        try
        {
            return adjacenceMatrix[from][to];
        }
        catch (ArrayIndexOutOfBoundsException index)
        {
            System.out.println("The vertices does not exists");

        }
        return -1;
    }
}
