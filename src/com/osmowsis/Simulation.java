package com.osmowsis;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Random;
import java.io.*;

public class Simulation {

    private static final int DEFAULT_MAX_SIZE = 100;

    private Lawn lawn;
    private ArrayList<LawnMower> mowers;
    private int maxTurns;
    private int numOfTurns;


    public Simulation() {
        mowers = new ArrayList<LawnMower>();
        numOfTurns = 0;
    }

    /**
     * Checks if there are turns left ( maxTurns - numOfTurns > 0)
     * AND checks that there is atleast 1 lawnmower active (mower.state == running)
     */
    public boolean hasTurn(){
        boolean hasTurn = false;
        if( maxTurns - numOfTurns > 0 ){
            int runningCount = 0;
            for(LawnMower mower : mowers) {
                if( mower.getState() == MowerState.running )
                    runningCount++;
            }

            if( runningCount > 0 ){
                hasTurn = true;
            }
        }
        return hasTurn;
    }

    public void loadStartingFile(String testFileName) {
        final String DELIMITER = ",";

        try {
            Scanner scanner = new Scanner(new File(testFileName));
            String[] tokens;

            int width, height;
            // read in the lawn information
            tokens = scanner.nextLine().split(DELIMITER);
            width = Integer.parseInt(tokens[0]);
            tokens = scanner.nextLine().split(DELIMITER);
            height = Integer.parseInt(tokens[0]);

            width = ( width <= 100 ) ? width : DEFAULT_MAX_SIZE;
            height = ( height <= 100 ) ? height : DEFAULT_MAX_SIZE;

            lawn = new Lawn( width, height );

            // read in the lawnmower starting information
            tokens = scanner.nextLine().split(DELIMITER);
            int numMowers = Integer.parseInt(tokens[0]);
            for (int i = 0; i < numMowers; i++) {
                tokens = scanner.nextLine().split(DELIMITER);
                int mowerX = Integer.parseInt(tokens[0]);
                int mowerY = Integer.parseInt(tokens[1]);
                String mowerDirection = tokens[2];

                lawn.addMower( mowerX, mowerY, i );
                mowers.add( new LawnMower( i, Direction.valueOf(mowerDirection) ) );
            }


            // read in the crater information
            tokens = scanner.nextLine().split(DELIMITER);
            int numCraters = Integer.parseInt(tokens[0]);
            for (int i = 0; i < numCraters; i++) {
                tokens = scanner.nextLine().split(DELIMITER);

                // place a crater at the given location
                int craterX = Integer.parseInt(tokens[0]);
                int craterY = Integer.parseInt(tokens[1]);
                lawn.addCrater(craterX, craterY);
            }

            // read in number of turns
            tokens = scanner.nextLine().split(DELIMITER);
            maxTurns = Integer.parseInt(tokens[0]);

            //set lawn number of grass
            lawn.setNumOfGrass( numMowers );

            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println();
        }
    }


    public void takeTurn(){

        //for each mower
        for(LawnMower mower : mowers) {

            //continue to next mower if current mower is not running.
            if( mower.getState() != MowerState.running ){
                continue;
            }

            //get action from mower
            Action action = mower.getAction();
            String simulationResponse = "";

            //validate action
            if( action.getState() == ActionState.scan ){
                simulationResponse = sendScanToMower( mower );
            }
            else{
                //set action in lawn model
                simulationResponse = lawn.moveMower( mower, action);
            }
            System.out.println( action.toString() );
            System.out.println( simulationResponse );

            //mower.printLawn();
        }

        numOfTurns++;
        //lawn.renderLawn();
    }

    public void printResults(){
        System.out.println( lawn.getNumberOfSquares() + "," +
                lawn.getNumberOfGrass() + "," +
                lawn.getNumberOfGrassCut() + "," +
                this.numOfTurns );
    }

    private String sendScanToMower(LawnMower mower){
        String scanResults = lawn.getScan( mower );
        mower.setScan( scanResults );
        return scanResults;
    }

}