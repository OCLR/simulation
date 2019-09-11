package org.wmbus.simulation.convergence.model;

import org.wmbus.simulation.convergence.model.config.AverageConvergenceConfigModel;
import org.wmbus.simulation.convergence.state.ConvergenceState;

public class AverageConvergenceModel extends ConvergenceModel {
    private AverageConvergenceConfigModel config;
    private double sum, index, average;
    public AverageConvergenceModel( AverageConvergenceConfigModel config) {
        this.config = config;
    }

    @Override
    public void reset() {
        super.reset();
        sum = 0;
        index = 0;
        average = 0;
    }

    @Override
    public int addMeasure(double measure) {
        double prevAverage = average;
        this.sum += measure;
        this.index++;
        this.average = this.sum/this.index;
        this.percentageConvergence = prevAverage==0?0:(Math.abs(average - prevAverage)/prevAverage)*100; // Default percentage at the beginning.

        if (this.percentageConvergence <= this.config.getPercentageConvergence()){
            return ConvergenceState.CONVERGENCE_STATE_STOP_CONVERGENCE;
        }else if (this.percentageConvergence > this.config.getPercentageNotConvergence()){
            return ConvergenceState.CONVERGENCE_STATE_STOP_NOTCONVERGENCE;
        }else{
            return ConvergenceState.CONVERGENCE_STATE_CONTINUE;
        }
    }
}
