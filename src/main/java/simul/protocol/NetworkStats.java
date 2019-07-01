package simul.protocol;

import simul.infrastructure.MbusNetwork;
import simul.messages.MbusMessage;

public class NetworkStats {

    private final MbusNetwork network;

    public NetworkStats(MbusNetwork network) {
        this.network = network;

    }

    public ResultTable printResults(){

        ResultTable r = new ResultTable();
        r.addRow("Network Hamming",SimulationConfiguration.CONF_HAMMING?1:0);
        r.addRow("Network BER",this.network.getBer());
        r.addRow("Network #slaves",this.network.getSlavesNum());
        r.addRow("Network #messages",this.network.getLasting());
        return r;
    }
}
