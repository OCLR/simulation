package org.wmbus.simulation;

import java.util.TreeMap;

public class ResultTable {
    TreeMap<String,Double> results = new TreeMap<String,Double>();
    public void addRow(String header,double value){
        this.results.put(header,value);
    }

    public String prettyPrint(){
        String result = "";

        for (String key: results.keySet()){
            result += (key+'\t'+results.get(key)+'\n');
        }
        return result;
    }

    public String printHeader(boolean csv){
        String result = "";
        if (csv){
            for (String key: results.keySet()){
                result+=(key+'\t');
            }
        }else {
            for (String key: results.keySet()) {
                result += (key + " ");
            }
        }
        if (result.length() != 0){
            return result.substring(0, result.length() - 1);
        }
        return "";
    }
    public String printValues(boolean csv){
        String result = "";
        if (csv){
            for (Double value: results.values()){
                result+=(""+value+'\t');
            }
        }else {
            for (Double value: results.values()) {
                result += (value + " ");
            }
        }
        if (result.length() != 0){
            return result.substring(0, result.length() - 1);
        }
        return "";
    }
}
