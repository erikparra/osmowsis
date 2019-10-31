package com.osmowsis;

import java.util.*;
import java.io.*;

public class Simulation {

    private static final int MAX_WIDTH = 15;
    private static final int MAX_HEIGHT = 10;
    private static final int MAX_MOWERS = 10;
    private static final int MAX_COLLISION_DELAY = 4;
    private static final int MAX_MOWER_ENERGY = 100;

    private Lawn lawn;
    private ArrayList<LawnMower> mowers;
    private int maxTurns;
    private int numOfTurns;


    public Simulation() {
        mowers = new ArrayList<LawnMower>();
        numOfTurns = 0;
    }

    // *** FOR UI STUFF
    public LawnSquare[][] getLawnState(){
        return lawn.getState();
    }

    public int getNumOfTurns(){
        return numOfTurns;
    }

    public List<LawnMower> getMowers(){return mowers; }

    public Point findMower(LawnMower m){return lawn.findMower(m);}
    // **


    /**
     * Checks if there are turns left ( maxTurns - numOfTurns > 0)
     * AND checks that there is atleast 1 lawnmower active (mower.state == running)
     */
    public boolean hasTurn(){
        boolean hasTurn = false;
        if( maxTurns - numOfTurns > 0 ){
            int runningCount = 0;
            for(LawnMower mower : mowers) {
                //added stalled state
                if( mower.getState() == MowerState.running || mower.getState() == MowerState.stalled )
                    runningCount++;
            }

            if( runningCount > 0 ){
                hasTurn = true;
            }
        }
        return hasTurn;
    }


    /**
     * 1. <the width (horizontal/X-direction) of the lawn>
     * 2. <the height (vertical/Y-direction) of the lawn>
     * 3. <the number of lawnmowers being used>
     * 4. <the mower “collision delay”: the number of turns stalled if it collides with another mower>
     * 5. <the (individual) mower energy capacity>
     * 6. <the initial location and direction of each lawnmower> [one line per lawnmower]
     * 7. <the number of craters on the lawn>
     * 8. <the location of each crater> [one line per crater]
     * 9. <the maximum number of turns for the simulation>
     */
    public void loadStartingFile(String testFileName) {
        final String DELIMITER = ",";

        try {
            Scanner scanner = new Scanner(new File(testFileName));
            String[] tokens;

            int width, height, numMowers, mowerDelay, mowerEnergy;
            // read in the lawn information
            tokens = scanner.nextLine().split(DELIMITER);
            width = Integer.parseInt(tokens[0]);
            tokens = scanner.nextLine().split(DELIMITER);
            height = Integer.parseInt(tokens[0]);
            tokens = scanner.nextLine().split(DELIMITER);
            numMowers = Integer.parseInt(tokens[0]);
            tokens = scanner.nextLine().split(DELIMITER);
            mowerDelay = Integer.parseInt(tokens[0]);
            tokens = scanner.nextLine().split(DELIMITER);
            mowerEnergy = Integer.parseInt(tokens[0]);


            width = ( width <= MAX_WIDTH ) ? width : MAX_WIDTH;
            height = ( height <= MAX_HEIGHT ) ? height : MAX_HEIGHT;
            numMowers = ( numMowers <= MAX_MOWERS ) ? numMowers : MAX_MOWERS;
            mowerDelay = ( mowerDelay <= MAX_COLLISION_DELAY ) ? mowerDelay : MAX_COLLISION_DELAY;
            mowerEnergy = ( mowerEnergy <= MAX_MOWER_ENERGY ) ? mowerEnergy : MAX_MOWER_ENERGY;


            lawn = new Lawn( width, height );

            // read in the lawnmower starting information
            for (int i = 0; i < numMowers; i++) {
                tokens = scanner.nextLine().split(DELIMITER);
                int mowerX = Integer.parseInt(tokens[0]);
                int mowerY = Integer.parseInt(tokens[1]);
                String mowerDirection = tokens[2];

                lawn.setMower( mowerX, mowerY, i+1, Direction.valueOf(mowerDirection));
                mowers.add( new LawnMower( i+1, Direction.valueOf(mowerDirection), mowerEnergy, mowerDelay ) );
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


    public List<ActionReport> takeTurn(){
        List<ActionReport> actionList = new LinkedList<>();
        //for each mower
        for(LawnMower mower : mowers) {

            switch( mower.getState() ){
                case crashed:
                case off:
                    continue;
                case stalled:
                    mower.performStalledTurn();
                    continue;
                case running:
                    //do nothing
                    break;
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
                simulationResponse = lawn.validateMove( mower, action);

                //mower will perform action
                mower.performMove( simulationResponse, action );
                //simulationResponse = lawn.moveMower( mower, action);
            }
            System.out.println( "mower_"+mower.getId() );
            System.out.println( action.toString() );
            System.out.println( simulationResponse );
            actionList.add(new ActionReport(action, mower.getId(), simulationResponse));
        }

        numOfTurns++;
        return actionList;
    }

    public void printResults(){
        System.out.println( lawn.getNumberOfSquares() + "," +
                lawn.getNumberOfGrass() + "," +
                lawn.getNumberOfGrassCut() + "," +
                this.numOfTurns );
    }

    public String getPrettyResults(){
        return String.format("Number of Squares: %d\nNumber of Grass Squares: %d\nNumber of Cut Grass Squares: %d\nNumber of Turns Taken: %d",
                lawn.getNumberOfSquares(),
                lawn.getNumberOfGrass(),
                lawn.getNumberOfGrassCut(),
                this.numOfTurns);
    }

    private String sendScanToMower(LawnMower mower){
        String scanResults = lawn.getScan( mower );
        //System.out.println("Sim-sendScanToMOwer: " + scanResults);
        mower.performScan( scanResults );
        return scanResults;
    }

}