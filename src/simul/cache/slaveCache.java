package simul.cache;

import java.util.ArrayList;

import simul.cache.base.cachePair;

public class slaveCache {
	// neighbor -> ArrayList<destination>.
    // less memory but bad performance.
    private ArrayList<cachePair> cache = new ArrayList<cachePair>();

	/**
	 * @return the cache
	 */
	public ArrayList<cachePair> getCache() {
		return cache;
	}

	/**
	 * @param cache the cache to set
	 */
	public void setCache(ArrayList<cachePair> cache) {
		this.cache = cache;
	}
	
	public int lookUpCache(int slaveAddress){
    	
    	for (cachePair cacheSingle : cache){
    		if (cacheSingle.getNextHops().contains(slaveAddress)){
    			return cacheSingle.getNeighbor();
    		}
    	}
    	return -1; // error case.
    }
    
	public void addLocalCache(Integer nextHop, Integer destination) {
    	boolean found = false;
    	for (cachePair cacheSingle : cache){
    		
    		if (cacheSingle.getNeighbor() == nextHop ){
    			found = true;
    			if (!cacheSingle.getNextHops().contains(destination)){
    				System.out.println("Add cache:"+cacheSingle.getNeighbor()+","+destination);
    				cacheSingle.getNextHops().add(destination);
    			}
    			// if contains ok.
    		}else{
    			// remove old record. if contains destination.
    			if (cacheSingle.getNextHops().contains(destination)){
    				System.out.println("Remove cache:"+cacheSingle.getNeighbor()+","+destination);
    				cacheSingle.getNextHops().remove(destination); // remove old hop.
    			}
    		}
    	}
    	if (!found){
    		cachePair cp = new cachePair(nextHop);
    		cp.getNextHops().add(destination);
    		cache.add(cp);
    	}
		
	}
}
