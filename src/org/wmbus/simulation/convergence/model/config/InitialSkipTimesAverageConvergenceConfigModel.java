package org.wmbus.simulation.convergence.model.config;

public class InitialSkipTimesAverageConvergenceConfigModel extends TimesAverageConvergenceConfigModel {
    private int initialSkip = 0;

    public InitialSkipTimesAverageConvergenceConfigModel(int initialSkip, double percentageConvergence, double percentageNotConvergence, int convergenceTimes, int notConvergenceTimes) {
        super(percentageConvergence, percentageNotConvergence, convergenceTimes, notConvergenceTimes);
        this.initialSkip = initialSkip;
    }

    public int getInitialSkip() {
        return initialSkip;
    }

    public void setInitialSkip(int initialSkip) {
        this.initialSkip = initialSkip;
    }
}
