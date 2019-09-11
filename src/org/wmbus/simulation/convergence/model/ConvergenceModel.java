package org.wmbus.simulation.convergence.model;

public  abstract  class ConvergenceModel {
    public double percentageConvergence;

    public void reset(){
        this.percentageConvergence = 0;
    }
    public abstract int addMeasure(double measure);
}
