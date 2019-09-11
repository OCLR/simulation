package org.wmbus.simulation.convergence.model;

import org.wmbus.simulation.convergence.model.config.InitialSkipTimesAverageConvergenceConfigModel;
import org.wmbus.simulation.convergence.state.ConvergenceState;

public class InitialSkipWithTimesAverageConvergenceModel extends TimesAverageConvergenceModel {
    private int initialSkip = 0;
    private InitialSkipTimesAverageConvergenceConfigModel config;

    public InitialSkipWithTimesAverageConvergenceModel(InitialSkipTimesAverageConvergenceConfigModel config) {
        super(config);
        this.config = config;
    }
    @Override
    public void reset() {
        super.reset();
        this.initialSkip = 0;
    }

    @Override
    public int addMeasure(double measure) {
        this.initialSkip++;

        if (this.initialSkip == this.config.getInitialSkip()) {
            // super.reset();
            super.partialReset();
        }
        int convergence = super.addMeasure(measure);
        if (this.initialSkip >= this.config.getInitialSkip()){
            // super.reset();
            return convergence;
        } else {
            return ConvergenceState.CONVERGENCE_STATE_CONTINUE;
        }

    }
}
