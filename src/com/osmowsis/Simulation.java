package com.osmowsis;

import java.util.Scanner;
import java.util.HashMap;
import java.util.Random;
import java.io.*;

public class Simulation {

    public Lawn lawn;




    private static Random randGenerator;

    private static final int DEFAULT_WIDTH = 100;
    private static final int DEFAULT_HEIGHT = 100;

    private Integer lawnHeight;
    private Integer lawnWidth;
    private Integer[][] lawnInfo;
    private Integer mowerX, mowerY;
    private String mowerDirection;
    private HashMap<String, Integer> xDIR_MAP;
    private HashMap<String, Integer> yDIR_MAP;

    private String trackAction;
    private Integer trackMoveDistance;
    private String trackNewDirection;
    private String trackMoveCheck;
    private String trackScanResults;

    private final int EMPTY_CODE = 0;
    private final int GRASS_CODE = 1;
    private final int CRATER_CODE = 2;

    public Simulation() {
        randGenerator = new Random();

        lawnHeight = 0;
        lawnWidth = 0;
        lawnInfo = new Integer[DEFAULT_WIDTH][DEFAULT_HEIGHT];
        mowerX = -1;
        mowerY = -1;
        mowerDirection = "North";

        xDIR_MAP = new HashMap<>();
        xDIR_MAP.put("North", 0);
        xDIR_MAP.put("Northeast", 1);
        xDIR_MAP.put("East", 1);
        xDIR_MAP.put("Southeast", 1);
        xDIR_MAP.put("South", 0);
        xDIR_MAP.put("Southwest", -1);
        xDIR_MAP.put("West", -1);
        xDIR_MAP.put("Northwest", -1);

        yDIR_MAP = new HashMap<>();
        yDIR_MAP.put("North", 1);
        yDIR_MAP.put("Northeast", 1);
        yDIR_MAP.put("East", 0);
        yDIR_MAP.put("Southeast", -1);
        yDIR_MAP.put("South", -1);
        yDIR_MAP.put("Southwest", -1);
        yDIR_MAP.put("West", 0);
        yDIR_MAP.put("Northwest", 1);
    }

    public boolean hasTurn(){
        return true;
    }

    public void loadStartingFile(String testFileName) {
        final String DELIMITER = ",";

        try {
            Scanner scanner = new Scanner(new File(testFileName));
            String[] tokens;

            int width, height;
            // read in the lawn information
            tokens = takeCommand.nextLine().split(DELIMITER);
            width = Integer.parseInt(tokens[0]);
            tokens = takeCommand.nextLine().split(DELIMITER);
            height = Integer.parseInt(tokens[0]);

            lawn = new Lawn( width, height );



            // read in the lawnmower starting information
            tokens = takeCommand.nextLine().split(DELIMITER);
            int numMowers = Integer.parseInt(tokens[0]);
            for (k = 0; k < numMowers; k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);
                mowerX = Integer.parseInt(tokens[0]);
                mowerY = Integer.parseInt(tokens[1]);
                mowerDirection = tokens[2];

                // mow the grass at the initial location
                lawnInfo[mowerX][mowerY] = EMPTY_CODE;
            }

            // read in the crater information
            tokens = takeCommand.nextLine().split(DELIMITER);
            int numCraters = Integer.parseInt(tokens[0]);
            for (k = 0; k < numCraters; k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);

                // place a crater at the given location
                lawnInfo[Integer.parseInt(tokens[0])][Integer.parseInt(tokens[1])] = CRATER_CODE;
            }

            takeCommand.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println();
        }
    }


    public void uploadStartingFile(String testFileName) {
        final String DELIMITER = ",";

        try {
            Scanner takeCommand = new Scanner(new File(testFileName));
            String[] tokens;
            int i, j, k;

            // read in the lawn information
            tokens = takeCommand.nextLine().split(DELIMITER);
            lawnWidth = Integer.parseInt(tokens[0]);
            tokens = takeCommand.nextLine().split(DELIMITER);
            lawnHeight = Integer.parseInt(tokens[0]);

            // generate the lawn information
            lawnInfo = new Integer[lawnWidth][lawnHeight];
            for (i = 0; i < lawnWidth; i++) {
                for (j = 0; j < lawnHeight; j++) {
                    lawnInfo[i][j] = GRASS_CODE;
                }
            }

            // read in the lawnmower starting information
            tokens = takeCommand.nextLine().split(DELIMITER);
            int numMowers = Integer.parseInt(tokens[0]);
            for (k = 0; k < numMowers; k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);
                mowerX = Integer.parseInt(tokens[0]);
                mowerY = Integer.parseInt(tokens[1]);
                mowerDirection = tokens[2];

                // mow the grass at the initial location
                lawnInfo[mowerX][mowerY] = EMPTY_CODE;
            }

            // read in the crater information
            tokens = takeCommand.nextLine().split(DELIMITER);
            int numCraters = Integer.parseInt(tokens[0]);
            for (k = 0; k < numCraters; k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);

                // place a crater at the given location
                lawnInfo[Integer.parseInt(tokens[0])][Integer.parseInt(tokens[1])] = CRATER_CODE;
            }

            takeCommand.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println();
        }
    }

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

    private void renderHorizontalBar(int size) {
        System.out.print(" ");
        for (int k = 0; k < size; k++) {
            System.out.print("-");
        }
        System.out.println("");
    }

    public void renderLawn() {
        int i, j;
        int charWidth = 2 * lawnWidth + 2;

        // display the rows of the lawn from top to bottom
        for (j = lawnHeight - 1; j >= 0; j--) {
            renderHorizontalBar(charWidth);

            // display the Y-direction identifier
            System.out.print(j);

            // display the contents of each square on this row
            for (i = 0; i < lawnWidth; i++) {
                System.out.print("|");

                // the mower overrides all other contents
                if (i == mowerX & j == mowerY) {
                    System.out.print("M");
                } else {
                    switch (lawnInfo[i][j]) {
                        case EMPTY_CODE:
                            System.out.print(" ");
                            break;
                        case GRASS_CODE:
                            System.out.print("g");
                            break;
                        case CRATER_CODE:
                            System.out.print("c");
                            break;
                        default:
                            break;
                    }
                }
            }
            System.out.println("|");
        }
        renderHorizontalBar(charWidth);

        // display the column X-direction identifiers
        System.out.print(" ");
        for (i = 0; i < lawnWidth; i++) {
            System.out.print(" " + i);
        }
        System.out.println("");

        // display the mower's direction
        System.out.println("dir: " + mowerDirection);
        System.out.println("");
    }

}