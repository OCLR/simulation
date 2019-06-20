package simul.nodes;

import java.util.Objects;

/**
 * Created by Federico Falconi on 05/07/2017.
 */
public class MasterGraphNode {
    private int staticAddress;

    public MasterGraphNode(int staticAddress) {
        this.staticAddress = staticAddress;
    }

    public int getStaticAddress() {
        return staticAddress;
    }

    public String toString() {
        return new Integer(staticAddress).toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null) return false;

        if (getClass() != obj.getClass())  return false;

        MasterGraphNode pair = (MasterGraphNode) obj;

        return Objects.equals(staticAddress, pair.staticAddress);
    }

    public int hashCode() {
        return new Integer(staticAddress).hashCode();
    }
}
