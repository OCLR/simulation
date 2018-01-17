package simul.cache.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class networkCache {
	private HashMap<Integer,nodeCache> cache = new HashMap<Integer,nodeCache>();

	/**
	 * @return the cache
	 */
	public HashMap<Integer,nodeCache> getCache() {
		return cache;
	}

	/**
	 * @param cache the cache to set
	 */
	public void setCache(HashMap<Integer,nodeCache> cache) {
		this.cache = cache;
	}

	public void store(Integer source, Integer hopDestination, Integer destination) {
		// TODO Auto-generated method stub
		nodeCache nc;
		if(!this.cache.containsKey(source)){
			nc = new nodeCache();
			nc.store(hopDestination, destination);
			this.cache.put(source, nc);
		}else{
			nc = this.cache.get(source);
			nc.store(hopDestination, destination);
		}
		
		
	}
	public boolean exists(Integer source, Integer hopDestination, Integer destination) {
		// TODO Auto-generated method stub
		if(!this.cache.containsKey(source)){
			return false;
		}
		if(this.cache.get(source).exists(hopDestination,destination)){
			System.out.println(source);
			return true;
		}else{
			return false;
		}
		
	}

	public void remove(Integer dest) {
		/*if(!this.cache.containsKey(source)){
			return false;
		}
		if(this.cache.get(source).remove(hopDestination,destination)){
			return true;
		}else{
			return false;
		}*/
		Iterator it = this.cache.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			nodeCache cp = (nodeCache) pairs.getValue();
			cp.remove(dest, dest);
		}
		
	}
}
