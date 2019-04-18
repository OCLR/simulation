/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simul.cache.base;

/**
 *
 * @author lvlz
 */
public class cachePacketTuple {

    public cachePacketTuple(int source, int nexthop) {
        this.source = source;
        this.nexthop = nexthop;
    }
    private int source;
    private int nexthop;

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getNexthop() {
        return nexthop;
    }

    public void setNexthop(int nexthop) {
        this.nexthop = nexthop;
    }

    
}
