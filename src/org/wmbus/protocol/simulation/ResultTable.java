package org.wmbus.protocol.simulation;

import java.util.HashMap;

public class ResultTable {
    HashMap<String,Double> results = new HashMap<String,Double>();
    public void addRow(String header,double value){
        this.results.put(header,value);
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
