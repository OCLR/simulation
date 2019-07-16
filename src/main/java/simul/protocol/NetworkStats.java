package simul.protocol;

import simul.infrastructure.WMbusNetwork;

public class NetworkStats {

    private final WMbusNetwork network;

    public NetworkStats(WMbusNetwork  network) {
        this.network = network;

    }

    public ResultTable printResults(){

        ResultTable r = new ResultTable();
        r.addRow("Network Hamming",SimulationConfiguration.CONF_HAMMING?1:0);
        r.addRow("Network #slaves",this.network.getSlavesNum());
        r.addRow("Network #messages",this.network.getLasting());
        return r;
    }
}
