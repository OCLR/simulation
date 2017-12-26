# OCLR Simulation
# Version 1.0
This simulation is based on a variant protocol of M-bus done by Prof. Rosario Culmone (University of Camerino).

The simulation can be found here: https://www.researchgate.net/publication/269645771_Light_Routing_Algorithm_for_Utility_Networks .

This variant concerns automatic meter reading applications ( e.g. water, gas and electric meter ).
The architecture considered is master/slave where the master has ideally unlimited computation capacity and power source .Instead the slave are limited.
## Variant Contribution
Since the noise is typically high in this kind of network, the slaves does not mantain a routing table. The packet flow is address by the packet itself. 
The master node provides the packet with the a precomputed route using Dijkstra algorithm.
The variant was implemented by Federico Falconi for his thesis on 2017-07-05. 
The first release (v1.0) concerns this variant and is released with license Eclipse Public License 1.0.
## Library Used
- Desmoj (Apache license 2.0)
- Jgraph (BSD 3)
- JgraphT (EPL 1.0)
- JgraphX (BSD 3)

