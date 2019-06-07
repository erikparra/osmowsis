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

    public boolean hasTurn(){
        //todo: check for turns
        //todo: check for mowers still running
        return (maxTurns - numOfTurns > 0);
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
            lawn.setNumOfGrass();

            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println();
        }
    }

    public void print(){
        System.out.println("Max Turns: " + maxTurns);
        lawn.renderLawn();
    }

    public void takeTurn(){

        //todo: for each mower
        //todo: get action
        //todo: validate action
        //todo: display action/resonse
        //todo: increase turn count
        //sim.pollMowerForAction();
        //sim.validateMowerAction();
        //sim.displayActionAndResponses();

        for(LawnMower mower : mowers) {
            System.out.println("Turn: " + numOfTurns + ", Mower: " + mower.getId() );
        }

        numOfTurns++;

    }

/*
    public void pollMowerForAction() {
        int moveRandomChoice;

        moveRandomChoice = randGenerator.nextInt(100);
        if (moveRandomChoice < 2) {
            // select turning off the mower as the action
            trackAction = "turn_off";
        } else if (moveRandomChoice < 10) {
            // select scanning as the action
            trackAction = "scan";
        } else {
            // select moving forward and the turning as the action
            trackAction = "move";

            // determine a distance
            moveRandomChoice = randGenerator.nextInt(100);
            if (moveRandomChoice < 20) {
                trackMoveDistance = 0;
            } else if (moveRandomChoice < 70) {
                trackMoveDistance = 1;
            } else {
                trackMoveDistance = 2;
            }

            // determine a new direction
            moveRandomChoice = randGenerator.nextInt(100);
            if (moveRandomChoice < 50) {
                switch (mowerDirection) {
                    case "South":
                        trackNewDirection = "Southwest";
                        break;
                    case "Southwest":
                        trackNewDirection = "West";
                        break;
                    case "West":
                        trackNewDirection = "Northwest";
                        break;
                    case "Northwest":
                        trackNewDirection = "North";
                        break;
                    case "Southeast":
                        trackNewDirection = "South";
                        break;
                    case "North":
                        trackNewDirection = "Northeast";
                        break;
                    case "Northeast":
                        trackNewDirection = "East";
                        break;
                    case "East":
                        trackNewDirection = "Southeast";
                        break;
                    default:
                        trackNewDirection = mowerDirection;
                        break;
                }
            } else {
                trackNewDirection = mowerDirection;
            }
        }
    }

    public void validateMowerAction() {
        int xOrientation, yOrientation;

        if (trackAction.equals("scan")) {
            // in the case of a scan, return the information for the eight surrounding squares
            // always use a northbound orientation
            trackScanResults = "empty,grass,crater,fence,empty,grass,crater,fence";

        } else if (trackAction.equals("move")) {
            // in the case of a move, ensure that the move doesn't cross craters or fences
            xOrientation = xDIR_MAP.get(mowerDirection);
            yOrientation = yDIR_MAP.get(mowerDirection);

            // just for this demonstration, allow the mower to change direction
            // even if the move forward causes a crash
            mowerDirection = trackNewDirection;

            int newSquareX = mowerX + trackMoveDistance * xOrientation;
            int newSquareY = mowerY + trackMoveDistance * yOrientation;

            if (newSquareX >= 0 & newSquareX < lawnWidth & newSquareY >= 0 & newSquareY < lawnHeight) {
                mowerX = newSquareX;
                mowerY = newSquareY;
                trackMoveCheck = "ok";

                // update lawn status
                lawnInfo[mowerX][mowerY] = EMPTY_CODE;
            } else {
                trackMoveCheck = "crash";
            }

        } else if (trackAction.equals("turn_off")) {
            trackMoveCheck = "ok";
        }
    }

    public void displayActionAndResponses() {
        // display the mower's actions
        System.out.print(trackAction);
        if (trackAction.equals("move")) {
            System.out.println("," + trackMoveDistance + "," + trackNewDirection);
        } else {
            System.out.println();
        }

        // display the simulation checks and/or responses
        if (trackAction.equals("move") | trackAction.equals("turn_off")) {
            System.out.println(trackMoveCheck);
        } else if (trackAction.equals("scan")) {
            System.out.println(trackScanResults);
        } else {
            System.out.println("action not recognized");
        }
    }

*/

}