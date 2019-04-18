package simul.cache.base;

import java.util.ArrayList;

class nodeCache {
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

	public void store(Integer hopDestination, Integer destination) {
		// TODO Auto-generated method stub
		Boolean found = false;
		if (hopDestination.equals(destination)){
			//Store it//return; // is the same, doesn't make sense.
			// But have in mind that the issue will occur another time.
		}
		for (cachePair cp:cache){
			if (cp.getNeighbor().equals(hopDestination)){
				// use new path.
				found = true;
				//System.out.println("Global cache add:"+hopDestination+","+destination);
				if (!cp.getNextHops().contains(destination)){
					cp.getNextHops().add(destination);// multiple times multiple problems :S 
				}
				
			}else{
				// remove old path.
				if (cp.getNextHops().contains(destination)){
					//System.out.println("Global cache remove:"+cp.getNeighbor()+","+destination);
					cp.getNextHops().remove(destination);
				}
			}
		}
		if (!found){
    		cachePair cp = new cachePair(hopDestination);
    		cp.getNextHops().add(destination);
    		cache.add(cp);
    	}
		
	}

	public boolean exists(Integer hopDestination, Integer destination) {
		
		for (cachePair cp:cache){
			if (cp.getNeighbor().equals(hopDestination) && cp.getNextHops().contains(destination)){
				//System.out.print("Cache hit! "+hopDestination+"|"+destination);
				// use new path.
				return true;
			}else{
				// remove 
			}
		}
		return false;
	}

	public boolean remove(Integer hopDestination, Integer destination) {
		for (cachePair cp:cache){
			if (cp.getNextHops().contains(destination)){
				cp.getNextHops().remove(destination); // remove the destination.
			}
				
		}
		return false;
	}
    
}
