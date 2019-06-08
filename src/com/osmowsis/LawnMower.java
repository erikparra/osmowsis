package com.osmowsis;

import com.osmowsis.*;
import java.awt.*;
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

    public Direction getDirection(){
        return this.direction;
    }

    public MowerState getState(){
        return this.state;
    }

    public int getId(){
        return this.id;
    }

    /**
     * Check if state running
     * check if selfmap complete (enclosed by fence
     *    and all grass
     */
    public Action getAction(){
        if( this.state != MowerState.running ){
            return null;
        }
        if( isLawnComplete() ){
            return null;
        }

        //first move is always scan
        if( knownlawn.size() == 1 ){
            return new Action(ActionState.scan);
        }
        else{

            // todo: scan current direction
            Action mowerAction = scanCurrentDirection();
            if( mowerAction == null ){
                return new Action(ActionState.move, 0, Direction.northeast);
            }
            else{
                return mowerAction;
            }
        }
    }


    public void setScan( String scanResults ){
        String[] values = scanResults.split(",");

        //north
        knownlawn.put( new Point(currentLocation.x, currentLocation.y+1), LawnState.valueOf( values[0] ) );

        //northeast
        knownlawn.put( new Point(currentLocation.x+1, currentLocation.y+1), LawnState.valueOf( values[1] ) );

        //east
        knownlawn.put( new Point(currentLocation.x+1, currentLocation.y), LawnState.valueOf( values[2] ) );

        //southeast
        knownlawn.put( new Point(currentLocation.x+1, currentLocation.y-1), LawnState.valueOf( values[3] ) );

        //south
        knownlawn.put( new Point(currentLocation.x, currentLocation.y-1), LawnState.valueOf( values[0] ) );

        //southwest
        knownlawn.put( new Point(currentLocation.x-1, currentLocation.y-1), LawnState.valueOf( values[0] ) );

        //west
        knownlawn.put( new Point(currentLocation.x-1, currentLocation.y), LawnState.valueOf( values[0] ) );

        //northwest
        knownlawn.put( new Point(currentLocation.x-1, currentLocation.y+1), LawnState.valueOf( values[0] ) );

    }

    /**
     * Verify there is a square of fence
     */
    private boolean isLawnComplete(){
        return false;
    }

    /**
     * return the state at the lawn location
     *    or null if not found
     */
    private LawnState checkLocation( Point p ){
        if( knownlawn.containsKey( p ) ){
            return knownlawn.get( p );
        }
        else{
            return null;
        }
    }

    /**
     * Return action if current direction has grass
     *   AND set current direction to empty
     * ELSE return null
     */
    private Action scanCurrentDirection(){
        Point newPoint;
        LawnState lawnState;
        switch ( direction ){
            case north:
                newPoint = new Point(currentLocation.x, currentLocation.y+1);
                lawnState = checkLocation( newPoint );
                if( lawnState == LawnState.grass ){
                    knownlawn.put(newPoint, LawnState.empty);
                    return new Action(ActionState.move, 1, this.direction);
                }
                else{
                    return null;
                }
            case northeast:
                newPoint = new Point(currentLocation.x+1, currentLocation.y+1);
                lawnState = checkLocation( newPoint );
                if( lawnState == LawnState.grass ){
                    knownlawn.put(newPoint, LawnState.empty);
                    return new Action(ActionState.move, 1, this.direction);
                }
                else{
                    return null;
                }
            case east:
                newPoint = new Point(currentLocation.x+1, currentLocation.y);
                lawnState = checkLocation( newPoint );
                if( lawnState == LawnState.grass ){
                    knownlawn.put(newPoint, LawnState.empty);
                    return new Action(ActionState.move, 1, this.direction);
                }
                else{
                    return null;
                }
            case southeast:
                newPoint = new Point(currentLocation.x+1, currentLocation.y-1);
                lawnState = checkLocation( newPoint );
                if( lawnState == LawnState.grass ){
                    knownlawn.put(newPoint, LawnState.empty);
                    return new Action(ActionState.move, 1, this.direction);
                }
                else{
                    return null;
                }
            case south:
                newPoint = new Point(currentLocation.x, currentLocation.y-1);
                lawnState = checkLocation( newPoint );
                if( lawnState == LawnState.grass ){
                    knownlawn.put(newPoint, LawnState.empty);
                    return new Action(ActionState.move, 1, this.direction);
                }
                else{
                    return null;
                }
            case southwest:
                newPoint = new Point(currentLocation.x-1, currentLocation.y-1);
                lawnState = checkLocation( newPoint );
                if( lawnState == LawnState.grass ){
                    knownlawn.put(newPoint, LawnState.empty);
                    return new Action(ActionState.move, 1, this.direction);
                }
                else{
                    return null;
                }
            case west:
                newPoint = new Point(currentLocation.x-1, currentLocation.y);
                lawnState = checkLocation( newPoint );
                if( lawnState == LawnState.grass ){
                    knownlawn.put(newPoint, LawnState.empty);
                    return new Action(ActionState.move, 1, this.direction);
                }
                else{
                    return null;
                }
            case northwest:
                newPoint = new Point(currentLocation.x-1, currentLocation.y+1);
                lawnState = checkLocation( newPoint );
                if( lawnState == LawnState.grass ){
                    knownlawn.put(newPoint, LawnState.empty);
                    return new Action(ActionState.move, 1, this.direction);
                }
                else{
                    return null;
                }
            default:
                return null;
        }
    }

}