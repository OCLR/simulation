package simul.base;

/**
 * Created by Federico Falconi on 29/06/2017.
 */


public class NetConfig {
    protected final int NODES;
    private double[][] adjacenceMatrix;

    NetConfig(int nodes) {
        NODES = nodes;
        adjacenceMatrix = new double[NODES][NODES];
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
