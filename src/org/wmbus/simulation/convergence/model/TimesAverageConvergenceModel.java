package org.wmbus.simulation.convergence.model;

import org.wmbus.simulation.convergence.model.config.TimesAverageConvergenceConfigModel;
import org.wmbus.simulation.convergence.state.ConvergenceState;

public class TimesAverageConvergenceModel extends AverageConvergenceModel {
    private TimesAverageConvergenceConfigModel config;
    private int convergenceTimes = 0;
    private int notConvergenceTimes;

    public TimesAverageConvergenceModel(TimesAverageConvergenceConfigModel config) {
        super(config);
        this.config = config;
    }

    @Override
    public void reset() {
        super.reset();
        convergenceTimes = 0;
        notConvergenceTimes = 0;
    }

    public void partialReset() {
        convergenceTimes = 0;
        notConvergenceTimes = 0;
    }


    @Override
    public int addMeasure(double measure) {
        int convergence = super.addMeasure(measure);
        if (convergence == ConvergenceState.CONVERGENCE_STATE_STOP_CONVERGENCE){
            this.convergenceTimes++;
            this.notConvergenceTimes = 0;
        }else {
            this.convergenceTimes = 0;
            this.notConvergenceTimes++;
        }
        if (this.notConvergenceTimes >= config.getNotConvergenceTimes()){
            return ConvergenceState.CONVERGENCE_STATE_STOP_NOTCONVERGENCE;
        } else if (this.convergenceTimes >= config.getConvergenceTimes()){
            return ConvergenceState.CONVERGENCE_STATE_STOP_CONVERGENCE;
        }else {
            return ConvergenceState.CONVERGENCE_STATE_CONTINUE;
        }

    }
}
