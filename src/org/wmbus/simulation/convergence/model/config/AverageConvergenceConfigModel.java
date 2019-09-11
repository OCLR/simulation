package org.wmbus.simulation.convergence.model.config;

public class AverageConvergenceConfigModel {
    private double percentageConvergence = 0;
    private double percentageNotConvergence = 0;

    public double getPercentageConvergence() {
        return percentageConvergence;
    }

    public void setPercentageConvergence(double percentageConvergence) {
        this.percentageConvergence = percentageConvergence;
    }

    public double getPercentageNotConvergence() {
        return percentageNotConvergence;
    }

    public void setPercentageNotConvergence(double percentageNotConvergence) {
        this.percentageNotConvergence = percentageNotConvergence;
    }

    public AverageConvergenceConfigModel(double percentageConvergence, double percentageNotConvergence) {
        this.percentageConvergence = percentageConvergence;
        this.percentageNotConvergence = percentageNotConvergence;
    }
}
