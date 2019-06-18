package simul.infrastructure;

import java.util.HashMap;

/**
 * Created by Federico Falconi on 05/07/2017.
 */

public class ECCTable {

    private int node;
    private HashMap<Integer, Double> entries;

    public ECCTable(int node) {
        this.entries = new HashMap<Integer, Double>();
        this.node = node;
    }

    public HashMap<Integer, Double> getEntries() {
        return entries;
    }

    public int getTableLength() {
        return entries.size() + 1;
    }

    public int getBytesSize(){
        return 4+((4+4)*entries.size());
    }
    public int getNode() {
        return node;
    }

}
