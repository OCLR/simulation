package simul.cache;

import java.util.ArrayDeque;
import java.util.ArrayList;

import simul.cache.base.networkCache;

public class masterCache {
	private networkCache cache = new networkCache();

	public ArrayDeque<Integer> reducePath(ArrayDeque<Integer> path) {
		Integer[] pathArr = path.toArray(new Integer[path.size()]);
		ArrayList<Integer> myPath = new ArrayList<Integer>();
		
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
					
					if (!myPath.contains(pathArr[i])){
						myPath.add(pathArr[i]);
					}
					if (!myPath.contains(pathArr[i+1])){
						myPath.add(pathArr[i+1]);
					}
					
				}
			}
		}
		myPath.remove(path.getLast());// Remove destination.
		//myPath.remove(pathArr[pathArr.length -2]);// Remove last neighbor (direct connected).
		
		return new ArrayDeque<Integer>(myPath);

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

}
