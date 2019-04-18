package simul.cache;

import java.util.ArrayDeque;
import java.util.ArrayList;
import simul.cache.base.cachePacketTuple;

import simul.cache.base.networkCache;

public class masterCache {
	private networkCache cache = new networkCache();

	public Object reducePath(ArrayDeque<Integer> path) {
		Integer[] pathArr = path.toArray(new Integer[path.size()]);
		//ArrayList<Integer> myPath = new ArrayList<Integer>();
		ArrayList<cachePacketTuple> myPath = new ArrayList<cachePacketTuple>();
		/*if (pathArr.length == 1){
			return path;
		}*/
		if (pathArr.length == 1 || pathArr.length == 2){
        	// means the node is a neighbor.
        	// the size is zero. 
        	// base case.
        	return new ArrayDeque<Integer>(); // empty hop list.
        	
                }
                
		for (int i = 0; i < pathArr.length; i++) {
			if (i + 1 != pathArr.length) {
				if (!cache.exists(pathArr[i], pathArr[i + 1], path.getLast())) {
					
					/*if (!myPath.contains(pathArr[i])){
						myPath.add(pathArr[i]);
					}
					if (!myPath.contains(pathArr[i+1])){
						myPath.add(pathArr[i+1]);
					}*/
                                        myPath.add(new cachePacketTuple(pathArr[i], pathArr[i+1]));
				}
			}
		}
                
		//myPath.remove(path.getLast());// Remove destination.
		//myPath.remove(pathArr[pathArr.length -2]);// Remove last neighbor (direct connected).
                // +2 beacuse i remove
                
                
		if (myPath.size() == 0 ){ // no cache or partial. || 
                    ArrayDeque<Integer> clone = path.clone();
                    clone.remove(path.getLast());
                    return clone;
                }else
                if ( evaluatePath(pathArr,myPath) ){ // myPath.size() != pathArr.length +1
                    // use partial cache or not ?
                    return myPath;
                    
                }else
                {
                    return new ArrayDeque<Integer>();
                }
                
                
                /*
                if ((myPath.size()*2) +1 >= path.size() ){ // consider the full path.
                    ArrayDeque<Integer> clone = path.clone();
                    clone.remove(path.getLast());
                    return clone;
                }else{
                    return myPath;
                }*/
                
		

	}

	public void applyCaching(ArrayDeque<Integer> path, Integer destination) {
		/**
		 * Save source -> destination
		 * 
		 */
		//path.removeLast(); // remove indeed destination.
		Integer[] pathArr = path.toArray(new Integer[path.size()]);
		for (int i = 0; i < pathArr.length; i++) {
			if (i + 1 != pathArr.length) {
				cache.store(pathArr[i], pathArr[i + 1], destination);
			}
		}
	}

	public void faultCaching(ArrayDeque<Integer> path, Integer destination) {
		/*Integer[] pathArr = path.toArray(new Integer[path.size()]);
		for (int i = 0; i < pathArr.length; i++) {
			if (i + 1 != pathArr.length) {
				
			}
		}*/
		cache.remove(destination); // remove everything
		
	}

    private boolean evaluatePath(Integer[] path, ArrayList<cachePacketTuple> myPath) {
        long longPath = 0;
        long shortPath = 0;
        int length = path.length;
        long cacheHit = 0;
        
        for (int hop = 1; hop < length; hop++){
            longPath+= length - hop ; // -1 for hop +1 for destination overhead.
            // Cache hit?
            Integer node = path[hop];
            if (virtualCacheHit(node,myPath)){
                cacheHit++;
            }
            shortPath += (length - (cacheHit*2) +1); // for destination.
        }
        
        return (shortPath > longPath);
        
    }

    private boolean virtualCacheHit(Integer node, ArrayList<cachePacketTuple> myPath) {
        for (cachePacketTuple packetTuple : myPath) {
            if (packetTuple.getSource() == node){
                return false;
            }
        }
        return true;
    }

}
