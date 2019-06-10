package com.osmowsis;

import com.osmowsis.*;

import java.util.HashMap;

public class LawnMower {

    private int id;
    private Direction direction;
    private MowerState state;
    private Point currentLocation;
    private HashMap<Point, LawnState> knownlawn = new HashMap<>();

    public LawnMower(int id, Direction dir) {
        this.id = id;
        this.direction = dir;
        this.state = MowerState.running;
        this.currentLocation = new Point(0, 0);
        this.knownlawn.put(currentLocation, LawnState.empty);
    }

    public Direction getDirection() {
        return this.direction;
    }

    public MowerState getState() {
        return this.state;
    }

    public int getId() {
        return this.id;
    }

    /**
     * Check if state running
     * check if selfmap complete (enclosed by fence
     * and all grass
     */
    public Action getAction() {
        if (this.state != MowerState.running) {
            return null;
        }

        if (isLawnComplete()) {
            return null;
        }

        //first move is always scan
        if (knownlawn.size() == 1) {
            return new Action(ActionState.scan);
        }
        else {

            // todo: scan current direction
            Action mowerAction = moveCurrentDirection();
            switch( mowerAction.getState() ){
                case crash:
                    return getNewDirection();
                case empty:
                    return getNewDirection();
                case unknown:
                    return new Action(ActionState.scan);
                default:
                    return mowerAction;
            }
        }
    }


    public void setScan(String scanResults) {
        String[] values = scanResults.split(",");

        for( Direction d : Direction.values() ){
            knownlawn.put(
                    new Point(currentLocation, d),
                    LawnState.valueOf( values[ d.getValue() ] )
            );
        }
    }

    public void printLawn(){
        for( Point key : knownlawn.keySet() ){
            System.out.println( "[ "+key.x+" , "+key.y+" ] : " + knownlawn.get(key).toString());
        }
    }

    /**
     * Verify there is a square of fence
     */
    private boolean isLawnComplete() {
        return false;
    }

    /**
     * return the state at the lawn location
     * or null if not found
     */
    private LawnState checkLocation(Point p) {
        if (knownlawn.containsKey(p)) {
            return knownlawn.get(p);
        } else {
            return null;
        }
    }


    /**
     * Return action if current direction has grass
     * AND set current direction to empty
     * ELSE return null
     */
    private Action moveCurrentDirection() {
        Point newPoint = new Point( currentLocation, direction);
        LawnState lawnState = checkLocation( newPoint );
        if( lawnState == null ){
            return new Action(ActionState.unknown);
        }
        switch ( lawnState ) {
            case grass:
                knownlawn.put(newPoint, LawnState.empty);
                currentLocation = newPoint;
                return new Action(ActionState.move, 1, this.direction);
            case crater:
            case fence:
            case mower:
                return new Action(ActionState.crash);
            case empty:
                return new Action(ActionState.empty);
            default:
                return new Action(ActionState.unknown);
        }
    }


    /**
     * Will scan the known areas around the mower and return the new direction it should travel, moving clockwise
     *
     * @return Action(move, 0, new_direction)
     */
    private Action getNewDirection() {
        LawnState lawnState;
        for( Direction d : Direction.values() ){
            lawnState = checkLocation( new Point( currentLocation, d) );
            if( lawnState == LawnState.grass ){
                this.direction = d;
                return new Action(ActionState.move, 0, d);
            }
        }

        //if all spaces around mower are id and blocking
        //  find direction of grass or missing spaces
        //  shutdown if all spaces are found.
        if( surroundingSpacesScanned() ){
            //FIND new direction towared missing grass or spaces
            return new Action(ActionState.scan);
        }
        else{
            return new Action(ActionState.scan);
        }

    }

    /**
     * Verify all spaces around the mower have been scanned
     *
     * @return boolean true if all are identified
     */
    private boolean surroundingSpacesScanned(){
        for( Direction d : Direction.values() ){
            LawnState lawnState = checkLocation( new Point( currentLocation, d) );
            if(lawnState == null ){
                return false;
            }
        }
        return true;
    }
}