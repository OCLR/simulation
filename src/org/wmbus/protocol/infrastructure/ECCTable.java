package org.wmbus.protocol.infrastructure;

import java.util.HashMap;

/**
 * Created by Federico Falconi on 05/07/2017.
 */

public class ECCTable {

    private int node;
    private HashMap<Integer, Integer> entries;

    public ECCTable(int node) {
        this.entries = new HashMap<Integer, Integer>();
        this.node = node;
    }

    public HashMap<Integer, Integer> getEntries() {
        return entries;
    }

    public int getTableLength() {
        return entries.size() + 1;
    }

    public int getBytesSize(){
        return 1+((1+1)*entries.size());
    }
    public int getNode() {
        return node;
    }

    public String toString(){
        String s = ""+this.node+",(";
        Integer si = this.entries.keySet().size();
        for (Integer key: this.entries.keySet()) {
            si--;
            if (si == 1){
                s+=(key+"["+this.entries.get(key).toString()+"]");
            }else {
                s+=(key+"["+this.entries.get(key).toString()+"],");
            }
        }
        s+=")";
        return s;
    }

}
