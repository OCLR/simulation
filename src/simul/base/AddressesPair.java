package simul.base;

import java.util.Objects;

/**
 * Created by Federico Falconi on 03/07/2017.
 */

public class AddressesPair {
    private int first;
    private int second;

    public AddressesPair(int first, int second) {
        this.first = first;
        this.second = second;
    }


    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null) return false;

        if (getClass() != obj.getClass())  return false;

        AddressesPair pair = (AddressesPair) obj;

        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }


    public int hashCode() {
        return new Integer(first + second).hashCode();
    }


    public String toString() {
        return "(" + first + ", " + second + ")";
    }


    public int getFirst() {
        return first;
    }


    public void setFirst(int first) {
        this.first = first;
    }


    public int getSecond() {
        return second;
    }


    public void setSecond(int second) {
        this.second = second;
    }
}

