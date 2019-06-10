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

            if( mowerAction.getState() == ActionState.crash ){
                return getNewDirection();
            }
            else if( mowerAction.getState() == ActionState.unknown ){
                return new Action(ActionState.scan);
            }
            else{
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
            //System.out.println("Direction: " + d + ", value: " + d.getValue() );
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

    private Action moveCurrentDirectionAction( Point p, LawnState ls ){
        switch (ls) {
            case grass:
                knownlawn.put(p, LawnState.empty);
                return new Action(ActionState.move, 1, this.direction);
            case crater:
            case fence:
            case mower:
                return new Action(ActionState.crash);
            default:
                return new Action(ActionState.unknown);
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
        return moveCurrentDirectionAction( newPoint, lawnState );
    }


    /**
     * Will scan the known areas around the mower and return the new direction it should travel, moving clockwise
     *
     * @return Action(move, 0, new_direction)
     */
    private Action getNewDirection() {
        //System.out.println("getNewDirection - Direction Length: " + Direction.values().length);
        Point newPoint;
        LawnState lawnState;
        for( Direction d : Direction.values() ){
            lawnState = checkLocation( new Point( currentLocation, d) );
            if( lawnState == LawnState.grass ){
                this.direction = d;
                return new Action(ActionState.move, 0, d);
            }
        }



        // all spaces around mower are empty,
        // find direction of grass in known state
        // or shut off because grass is complete

        System.out.println("Spaces around mower are empty, find direction toward grass");
        return new Action(ActionState.scan);
    }
}