/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simul.protocol;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author lvlz
 */
public class CommunicationFault extends  Exception{

    private final long node;
    private ArrayDeque<NoiseTable> tables = new ArrayDeque<NoiseTable>();
    private boolean unRecoverable = false;

    public boolean isUnRecoverable() {
        return unRecoverable;
    }

    public void setUnRecoverable(boolean unRecoverable) {
        this.unRecoverable = unRecoverable;
    }
    public ArrayDeque<NoiseTable> getTables() {
        return tables;
    }

    public void setTables(ArrayDeque<NoiseTable> tables) {
        this.tables = tables;
    }

    public CommunicationFault(long slaveAddress) {
        this.node = slaveAddress;
        this.unRecoverable = false;
    }

    public long getNode(){
        return this.node;
    }
    
}
