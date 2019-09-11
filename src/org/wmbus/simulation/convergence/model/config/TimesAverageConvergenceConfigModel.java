package org.wmbus.simulation.convergence.model.config;

public class TimesAverageConvergenceConfigModel extends AverageConvergenceConfigModel{

    private int ConvergenceTimes = 0;
    private int notConvergenceTimes = 0;

    public int getConvergenceTimes() {
        return ConvergenceTimes;
    }

    public void setConvergenceTimes(int convergenceTimes) {
        ConvergenceTimes = convergenceTimes;
    }

    public int getNotConvergenceTimes() {
        return notConvergenceTimes;
    }

    public void setNotConvergenceTimes(int notConvergenceTimes) {
        this.notConvergenceTimes = notConvergenceTimes;
    }

    public TimesAverageConvergenceConfigModel(double percentageConvergence, double percentageNotConvergence, int convergenceTimes, int notConvergenceTimes) {
        super(percentageConvergence, percentageNotConvergence);
        ConvergenceTimes = convergenceTimes;
        this.notConvergenceTimes = notConvergenceTimes;
    }
}
