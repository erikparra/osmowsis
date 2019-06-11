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

            //run turns on simulation
            while( sim.hasTurn() ) {
                sim.takeTurn();
            }

            sim.printResults();

        }
    }
}