package simul.protocol;

import java.util.HashMap;

/**
 * Created by Federico Falconi on 05/07/2017.
 */

public class NoiseTable {
    private int node;
    private HashMap<Integer, Double> entries;

    public NoiseTable(HashMap<Integer, Double> entries, int node) {
        this.entries = new HashMap<Integer, Double>(entries);
        this.node = node;
    }

    public HashMap<Integer, Double> getEntries() {
        return entries;
    }

    public int getTableLength() {
        return entries.size() + 1;
    }
}
