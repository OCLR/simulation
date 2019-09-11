package org.wmbus;
import org.apache.commons.cli.*;
import org.wmbus.simulation.WMbusSimulationConfig;
import org.wmbus.simulation.convergence.model.config.InitialSkipTimesAverageConvergenceConfigModel;

public class CliMain {

    public static void main(String[] args) {
        Options options = new Options();

        Option input = new Option("i", "input", true, "Input adjacency network");
        input.setRequired(true);
        input.setType(String.class);
        options.addOption(input);

        Option convergenceTime = new Option("ct", "convergencetimes", true, "Convergence times");
        convergenceTime.setType(Integer.class);
        options.addOption(convergenceTime);

        Option notConvergenceTime = new Option("nct", "notconvergencetimes", true, "Not Convergence times");
        notConvergenceTime.setType(Integer.class);
        options.addOption(notConvergenceTime);

        Option convergencePercentage = new Option("cp", "convergencepercentage", true, "Convergence percentage");
        convergencePercentage.setType(Double.class);
        options.addOption(convergencePercentage);

        Option notConvergencePercentage = new Option("ncp", "notconvergencepercentage", true, "Not Convergence percentage");
        notConvergencePercentage.setType(Double.class);
        options.addOption(notConvergencePercentage);

        Option skippingStep = new Option("ss", "skippingstep", true, "Skipping step before try evaluate convergence.");
        skippingStep.setType(Integer.class);
        options.addOption(skippingStep);

        Option withHamming = new Option("wh", "withhamming", false, "With hamming function");
        withHamming.setType(Boolean.class);
        options.addOption(withHamming);

        Option withWakeUpFunction = new Option("ww", "withwakeupfunction", false, "With wakeup algorithm.");
        withWakeUpFunction.setType(Boolean.class);
        options.addOption(withWakeUpFunction);

        Option withDetailedNoise = new Option("wp", "withdetailednoise", false, "With detailed noise communication.");
        withDetailedNoise.setType(Boolean.class);
        options.addOption(withDetailedNoise);

        // Parsing stuff.
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
            }
        catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }

        boolean withHammingOption  = Boolean.parseBoolean(cmd.getOptionValue("withhamming","true"));
        boolean withWakeUpOption        = Boolean.parseBoolean(cmd.getOptionValue("withwakeupfunction" ,"true"));
        boolean withDetailedNoiseOption  = Boolean.parseBoolean(cmd.getOptionValue("withdetailednoise", "true"));

        int skippingStepOption = Integer.parseInt(cmd.getOptionValue("skippingstep", "1000"));
        double notConvergencePercentageOption = Double.parseDouble(cmd.getOptionValue("notconvergencepercentage", "5"));
        double convergencePercentageOption = Double.parseDouble(cmd.getOptionValue("convergencepercentage", "1"));
        int notConvergenceTimeOption = Integer.parseInt(cmd.getOptionValue("notconvergencetimes", "10"));
        int convergenceTimeOption = Integer.parseInt(cmd.getOptionValue("convergencetimes", "10"));
        String inputOption = (cmd.getOptionValue("input"));

        WMbusSimulationConfig config =  new WMbusSimulationConfig(
                withHammingOption,
                withWakeUpOption,
                withDetailedNoiseOption
        );
        InitialSkipTimesAverageConvergenceConfigModel convergenceModelConfig =  new InitialSkipTimesAverageConvergenceConfigModel(
            skippingStepOption, convergencePercentageOption,notConvergencePercentageOption, convergenceTimeOption, notConvergenceTimeOption
        );


        // WmbusStats stats = WMBusSimulationHelper.performSimulation();

    }
}
