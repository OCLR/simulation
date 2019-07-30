package org.wmbus.protocol.infrastructure;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.pmw.tinylog.Logger;
import org.wmbus.protocol.config.WMBusMasterConfig;
import org.wmbus.protocol.utilities.DGraph;
import org.wmbus.simulation.WMBusSimulation;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


class BackOffPath implements Comparable{
    public ArrayList<Integer> path;
    public int destination;
    public double totalWeight;
    public Integer failedAttempt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BackOffPath)) return false;
        BackOffPath that = (BackOffPath) o;
        return this.path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    @Override
    public int compareTo(Object o) {
        if (this == o) return 0;
        if (!(o instanceof BackOffPath)) return -1;
        BackOffPath that = (BackOffPath) o;

        if(this.totalWeight==that.totalWeight) {
            if (this.path.size() == that.path.size()) {
                return 0;
            } else if (this.path.size() > that.path.size()) {
                return 1;
            }else{
                return -1;
            }
        }else if(this.totalWeight>that.totalWeight)
            return 1;
        else
            return -1;
    }
}

public class PathChooser {
    /* a path  to backOff probability. */
    private ArrayList<BackOffPath> backOffPaths = new ArrayList<BackOffPath>();
    private WMBusSimulation simulation;
    private Integer lastChoosed = 0;
    public PathChooser(WMBusSimulation simulation) {
        this.simulation = simulation;
    }


    public ArrayList<Integer> searchPathWithBackOff(SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge>   graph, Integer destination){
        ArrayList<BackOffPath> backPP = new ArrayList<BackOffPath>();
        AllDirectedPaths<Integer, DefaultWeightedEdge> dijkstra  = new AllDirectedPaths<Integer, DefaultWeightedEdge>(graph);
        List<GraphPath<Integer, DefaultWeightedEdge>> allPaths = dijkstra.getAllPaths((0), (destination),true,null);
        BackOffPath bpath;

        // all possible path between A and B. A --> master and B -> destination node.
        for (GraphPath<Integer, DefaultWeightedEdge> path :allPaths){

            bpath = new BackOffPath();
            bpath.path = DGraph.getPath(path.getGraph(),path.getEdgeList());
            System.out.println("bpath-"+bpath.path);
            if (bpath.path.get(0) != 0){
                continue; // avoid this path.
            }
            bpath.failedAttempt = 0;
            bpath.totalWeight = path.getWeight();
            bpath.destination = path.getEndVertex();
            // Check bpath.
            if (this.backOffPaths.contains(bpath)){
                Integer bi = this.backOffPaths.indexOf(bpath);
                this.backOffPaths.get(bi).totalWeight = bpath.totalWeight;
                // update the weight.
            }else{
                this.backOffPaths.add(bpath);
            }
            // We have to mantain backOffPaths.
            // find the correct path to be associated to path.
        }
        for (BackOffPath backOff:this.backOffPaths){
            if (backOff.destination == destination){
                System.out.println("bpath considered-"+backOff.path);
                backPP.add(backOff);
            }
        }
        Collections.sort(backPP,Collections.reverseOrder());
        // The last one is the best path.
        for ( int bp = this.lastChoosed; bp < backPP.size() -1 ;bp++) {
            Integer failedAttempt = backPP.get(bp).failedAttempt;
            int doubleExp = (failedAttempt+ WMBusMasterConfig.BACKOFF_ATTEMPT_START);
            if (doubleExp > WMBusMasterConfig.BACKOFF_ATTEMPT_MAX) {
                doubleExp %= WMBusMasterConfig.BACKOFF_ATTEMPT_MAX;
                doubleExp += WMBusMasterConfig.BACKOFF_ATTEMPT_START;
            }
            Double probability = Math.pow(2,-doubleExp);
            if (probability <= this.simulation.getwMbusSimulationConfig().CONF_RANDOM.nextDouble()) {
                lastChoosed = bp + 1;
                return new ArrayList<Integer>(backPP.get(bp).path);
            }
            // 2^-5(1/32 to 1/4096)
        }
        lastChoosed = 0;

        return new ArrayList<Integer>(backPP.get(backPP.size()-1).path);
    }

    public ArrayList<Integer> searchPathWithoutBackOff(SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge>   graph, Integer destination){
        DijkstraShortestPath dijkstraPaths = new DijkstraShortestPath<Integer, DefaultWeightedEdge>(graph);
        //salesman.getTour(graph)
        // dijkstraPaths.getPaths(masterNode);
        Integer endNode = (destination); // chooose destination and apply dijkstra
        //System.out.println("WMBusMaster ask data to "+fixedNode);
        if (endNode == null) {
            throw new IllegalArgumentException();
        }
        /**
         * The dijkstraPaths path.
         */
        GraphPath p = null;
        try {
            p = dijkstraPaths.getPath((0),endNode);
        } catch (Exception e) {
            //throw new SUSPEND;
            Logger.error("Avoid packet with endnode: "+destination);
            //System.out.println(e.getMessage());
            //continue;
        }
        if (p == null){
            Logger.error("Path not found");
        }
        return new ArrayList<Integer>(DGraph.getPath(graph,p.getEdgeList()));
    }
    public ArrayList<Integer> searchPath(SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge>   graph, Integer destination, boolean backOff){
        if (backOff) {
            return this.searchPathWithBackOff(graph,destination);
        }else {
            return this.searchPathWithoutBackOff(graph,destination);
        }

    }
}
