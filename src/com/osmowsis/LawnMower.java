package com.osmowsis;

import com.osmowsis.*;

import java.util.*;
import java.util.stream.Collectors;

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
            //System.out.println("COMPLETE");
            this.state = MowerState.off;
            return new Action(ActionState.scan);
        }

        //first move is always scan
        if (knownlawn.size() == 1) {
            return new Action(ActionState.scan);
        } else {

            // todo: scan current direction
            Action mowerAction = moveCurrentDirection();
            switch (mowerAction.getState()) {
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

        for (Direction d : Direction.values()) {
            knownlawn.put(
                    new Point(currentLocation, d),
                    LawnState.valueOf(values[d.getValue()])
            );
        }
    }

    public void printLawn() {
        LinkedHashMap<Point, LawnState> sorted = knownlawn.entrySet().stream()
                .sorted((p1, p2) -> {
                    if (p1.getKey().x == p2.getKey().x) {
                        return p1.getKey().y - p2.getKey().y;
                    } else {
                        return p1.getKey().x - p2.getKey().x;
                    }
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        for (Point key : sorted.keySet()) {
            //System.out.println( key );
            System.out.println("[\t" + key.x + "\t,\t" + key.y + "\t] : " + knownlawn.get(key).toString());
        }
    }


    /**
     * Verify there is a square of fence
     *   check bottom left has north and east fence
     *   check top right has south and west fence
     */
    private boolean isLawnComplete() {
        // sort to get bottom left corner
        Point bottomLeft = getBottomLeftPoint();
        Point topRight = getTopRightPoint();

        if( bottomLeft.equals(topRight ) ){
            return false;
        }


        LawnState north = checkLocation( new Point( bottomLeft, Direction.north) );
        LawnState east = checkLocation( new Point( bottomLeft, Direction.east) );

        LawnState south = checkLocation( new Point( topRight, Direction.south) );
        LawnState west = checkLocation( new Point( topRight, Direction.west) );

        LawnState bottom = checkLocation( bottomLeft );
        LawnState top = checkLocation( topRight );

        if( north != LawnState.fence ||
                east != LawnState.fence ||
                south != LawnState.fence ||
                west != LawnState.fence ||
                bottom != LawnState.fence ||
                top != LawnState.fence )
        return false;


        for( int x = bottomLeft.x; x < topRight.x; x++ ){
            for( int y = bottomLeft.y; y < topRight.y; y++ ){
                Point testPoint = new Point(x, y);
                LawnState testLawnState = checkLocation(testPoint);
                if( testLawnState == null || testLawnState == LawnState.grass ){
                    return false;
                }
            }
        }

        return true;
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
        Point newPoint = new Point(currentLocation, direction);
        LawnState lawnState = checkLocation(newPoint);
        if (lawnState == null) {
            return new Action(ActionState.unknown);
        }
        switch (lawnState) {
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
        for (Direction d : Direction.values()) {
            lawnState = checkLocation(new Point(currentLocation, d));
            if (lawnState == LawnState.grass) {
                this.direction = d;
                return new Action(ActionState.move, 0, d);
            }
        }

        // if all spaces around mower are id and empty
        //  find direction of grass or missing spaces
        //  shutdown if all spaces are found.

        if (surroundingSpacesScanned()) {
            Point nextPoint = getFirstGrassPoint();
            if (nextPoint == null) {
                nextPoint = getMissingAreaPoint();
                if( nextPoint == null ){
                    this.state = MowerState.off;
                    return new Action(ActionState.scan);
                }
                else{
                    return directionTowardPoint( nextPoint );
                }
            }
            else{
                return directionTowardPoint( nextPoint );
            }
        } else {
            return new Action(ActionState.scan);
        }

    }

    private Direction degreeToDirection( double degrees ){
        //north
        if( degrees <= (90+(11.25*2)) && degrees > (90-(11.25*2)) ){
            return Direction.north;
        }
        //northeast
        else if( degrees <= (45+(11.25*2)) && degrees > (45-(11.25*2)) ){
            return Direction.northeast;
        }
        //east
        else if( degrees <= (0+(11.25*2)) && degrees > (0-(11.25*2)) ){
            return Direction.east;
        }
        //southeast
        else if( degrees <= (-45+(11.25*2)) && degrees > (-45-(11.25*2)) ){
            return Direction.southeast;
        }
        //south
        else if( degrees <= (-90+(11.25*2)) && degrees > (-90-(11.25*2)) ){
            return Direction.south;
        }
        //southwest
        else if( degrees <= (-135+(11.25*2)) && degrees > (-135-(11.25*2)) ){
            return Direction.southwest;
        }
        //west
        else if( degrees <= (180+(11.25*2)) && degrees > (180-(11.25*2)) ){
            return Direction.west;
        }
        //northwest
        else if( degrees <= (135+(11.25*2)) && degrees > (45-(11.25*2)) ){
            return Direction.northwest;
        }
        else{
            System.out.println("ERROR: SHOULD NOT BE HERE LawnMower.degreeToDirection LAST ELSE");
            return Direction.north;
        }
    }

    private Action directionTowardPoint( Point destPoint ){

        //System.out.println( currentLocation );
        //System.out.println( destPoint );

        double radians = Math.atan2( destPoint.y - currentLocation.y, destPoint.x - currentLocation.x);
        double degrees = Math.toDegrees( radians );

        Direction nextDirection = degreeToDirection( degrees );
        Point nextPoint = new Point( currentLocation, nextDirection);
        LawnState nextLawnState = checkLocation( nextPoint );
        while( nextLawnState != LawnState.grass && nextLawnState != LawnState.empty ){
            degrees = degrees = (11.25*2);
            nextDirection = degreeToDirection( degrees );
            nextLawnState = checkLocation( new Point( currentLocation, nextDirection));
        }

        if( this.direction == nextDirection ){
            knownlawn.put(nextPoint, LawnState.empty);
            currentLocation = nextPoint;
            return new Action(ActionState.move, 1, this.direction);
        }
        else{
            this.direction = nextDirection;
            return new Action(ActionState.move, 0, nextDirection);
        }
    }

    /**
     * Scan map for missing area or grass
     *
     */
    private Point getMissingAreaPoint() {
        Point bottomLeft = getBottomLeftPoint();
        Point topRight = getTopRightPoint();

        for( int x = bottomLeft.x; x < topRight.x; x++ ){
            for( int y = bottomLeft.y; y < topRight.y; y++ ){
                Point testPoint = new Point(x, y);
                LawnState testLawnState = checkLocation(testPoint);
                if( testLawnState == null || testLawnState == LawnState.grass ){
                    return testPoint;
                }
            }
        }
        return null;
    }

    private Point getBottomLeftPoint() {
        LinkedHashMap<Point, LawnState> sorted = knownlawn.entrySet().stream()
                .sorted((p1, p2) -> {
                    if (p1.getKey().x == p2.getKey().x) {
                        return p1.getKey().y - p2.getKey().y;
                    } else {
                        return p1.getKey().x - p2.getKey().x;
                    }
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return sorted.entrySet().stream().findFirst().get().getKey();
    }

    private Point getTopRightPoint() {
        LinkedHashMap<Point, LawnState> sorted = knownlawn.entrySet().stream()
                .sorted((p1, p2) -> {
                    if (p1.getKey().x == p2.getKey().x) {
                        return p2.getKey().y - p1.getKey().y;
                    } else {
                        return p2.getKey().x - p1.getKey().x;
                    }
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return sorted.entrySet().stream().findFirst().get().getKey();
    }

    /**
     * @return Point First instance of grass in known map, or null if not found
     */
    private Point getFirstGrassPoint() {
        HashMap<Point, LawnState> grassPoints = knownlawn.entrySet()
                .stream()
                .filter(map -> map.getValue() == LawnState.grass)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (prev, next) -> next, LinkedHashMap::new));
        if (grassPoints.isEmpty()) {
            return null;
        } else {
            return grassPoints.entrySet().stream().findFirst().get().getKey();
        }
    }

    /**
     * Verify all spaces around the mower have been scanned
     *
     * @return boolean true if all are identified
     */
    private boolean surroundingSpacesScanned() {
        for (Direction d : Direction.values()) {
            LawnState lawnState = checkLocation(new Point(currentLocation, d));
            if (lawnState == null) {
                return false;
            }
        }
        return true;
    }
}