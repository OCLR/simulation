/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simul.protocol;

import simul.infrastructure.ECCTable;

import java.util.ArrayDeque;

/**
 *
 * @author lvlz
 */
public class CommunicationFault extends  Exception{

    private final long node;
    private ArrayDeque<ECCTable> tables = new ArrayDeque<ECCTable>();
    private boolean unRecoverable = false;

    public boolean isUnRecoverable() {
        return unRecoverable;
    }

    public void setUnRecoverable(boolean unRecoverable) {
        this.unRecoverable = unRecoverable;
    }
    public ArrayDeque<ECCTable> getTables() {
        return tables;
    }

    public void setTables(ArrayDeque<ECCTable> tables) {
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
