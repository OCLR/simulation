package simul.cache.base;

import java.util.ArrayList;

public class cachePair {
	private Integer neighbor;
	private ArrayList<Integer> nextHops = new ArrayList<Integer>();
	
	public cachePair(Integer neighbor) {
		this.neighbor = neighbor;
		
	}
	/**
	 * @return the nextHops
	 */
	public ArrayList<Integer> getNextHops() {
		return nextHops;
	}
	/**
	 * @param nextHops the nextHops to set
	 */
	public void setNextHops(ArrayList<Integer> nextHops) {
		this.nextHops = nextHops;
	}
	/**
	 * @return the neighbor
	 */
	public Integer getNeighbor() {
		return neighbor;
	}
	/**
	 * @param neighbor the neighbor to set
	 */
	public void setNeighbor(Integer neighbor) {
		this.neighbor = neighbor;
	}
	
	
}
