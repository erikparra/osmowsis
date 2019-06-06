package com.osmowsis;

public class Main {

    public static void main(String[] args) {

        Simulation sim = new Simulation();

        if( args.length == 0) {
            System.out.println("ERROR: Test scenario file name not found.");
        }
        else {

            //loan simulation file
            sim.loadStartingFile(args[0]);

            //run turns on simulatin
            while( sim.hasTurn() ) {
                sim.pollMowerForAction();
                sim.validateMowerAction();
                sim.displayActionAndResponses();

                //TODO: comment out the rendering before submission
                sim.renderLawn();

                // pause after each event for a given number of seconds
                // pause is completely optional
                try {
                    Thread.sleep(5000);
                }
                catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}