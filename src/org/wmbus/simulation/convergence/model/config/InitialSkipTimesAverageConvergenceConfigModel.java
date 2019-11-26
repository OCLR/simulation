package org.wmbus.simulation.convergence.model.config;

public class InitialSkipTimesAverageConvergenceConfigModel extends TimesAverageConvergenceConfigModel {
    private long initialSkip = 0;

    public InitialSkipTimesAverageConvergenceConfigModel(long initialSkip, double percentageConvergence, double percentageNotConvergence, int convergenceTimes, int notConvergenceTimes) {
        super(percentageConvergence, percentageNotConvergence, convergenceTimes, notConvergenceTimes);
        this.initialSkip = initialSkip;
    }

    public long getInitialSkip() {
        return initialSkip;
    }

    public void setInitialSkip(int initialSkip) {
        this.initialSkip = initialSkip;
    }
}
