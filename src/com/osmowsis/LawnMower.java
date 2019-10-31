package com.osmowsis;

import java.util.*;
import java.util.stream.Collectors;
import java.awt.geom.Point2D;

public class LawnMower {

    private int MAX_ENERGY;
    private int MAX_DELAY;

    private int id;
    private Direction direction;
    private MowerState state;
    private Point currentLocation;  //relative to its starting location (0,0)
    //private HashMap<Point, LawnState> knownlawn = new HashMap<>();
    private int energy;
    private SharedLawn sharedLawn;
    private int stalledCount;

    private boolean justStalled;

    public LawnMower(int id, Direction dir, int energy, int delay) {
        this.id = id;
        this.direction = dir;
        this.state = MowerState.running;
        this.currentLocation = new Point(0, 0);
        this.energy = energy;
        this.MAX_ENERGY = energy;
        this.MAX_DELAY = delay;
        this.justStalled = false;

        sharedLawn = SharedLawn.getInstance();
        sharedLawn.registerNewMower(id, currentLocation, new LawnSquareMower(LawnState.energymower, id, dir));
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

    public int getEnergy() {
        return this.energy;
    }

    public void resetEnergy() {
        this.energy = this.MAX_ENERGY;
    }

    public int getStalledCount() {
        return this.stalledCount;
    }

    public void performStalledTurn() {
        this.justStalled = true;
        this.stalledCount--;
        this.energy--;
        if (this.stalledCount == 0) {
            this.state = MowerState.running;
        }

        checkforRecharge();
        if (this.energy <= 0) {
            this.state = MowerState.crashed;
        }
    }

    public void checkforRecharge() {
        LawnState currentLawnState = checkLocation(currentLocation);
        if (currentLawnState == LawnState.energy || currentLawnState == LawnState.energymower) {
            this.energy = this.MAX_ENERGY;
        }

        if( this.energy <= 0 ){
            this.state = MowerState.crashed;
        }
    }

    public void reduceEnergyUsage(Action action) {
        if (action.getSteps() >= 1) {
            this.energy -= 2;
        } else {
            this.energy--;
        }
    }

    public void performMove(String lawnRepsonse, Action action) {

        reduceEnergyUsage(action);

        if (lawnRepsonse.contains("crash")) {
            this.state = MowerState.crashed;
        } else if (lawnRepsonse.contains("stall")) {
            this.state = MowerState.stalled;
            this.stalledCount = this.MAX_DELAY;
        } else {
            if (action.getSteps() >= 1) {
                this.sharedLawn.moveMower(this.id, this.currentLocation, this.direction);
                this.currentLocation = new Point(currentLocation, this.direction);
//                this.sharedLawn.addPoint(this.id, this.currentLocation, new LawnSquareMower(this.sharedLawn.getLawn(this.id).get(this.currentLocation).getState(), this.id, this.direction));
            }
            this.direction = action.getDirection();
        }

        checkforRecharge();
    }

    /**
     * Check if state running
     * check if selfmap complete (enclosed by fence
     * and all grass
     */
    public Action getAction() {
        //if (this.state != MowerState.running) {
        //    return null;

        if (isLawnComplete()) {
            this.state = MowerState.off;
            return new Action(ActionState.scan);
        }

        if( justStalled ){
            justStalled = false;
            return new Action(ActionState.scan);
        }
        
        //first move is always scan
        if (sharedLawn.getLawn(this.id).size() == 1) {
            return new Action(ActionState.scan);
        } else {

            //printSelfLawn();

            if (energy < (MAX_ENERGY / 2)) {
                //return to energy pad
                Point nearestEnergy = getNearestEnergy();
                Action potentialA =  directionTowardPoint(nearestEnergy);
                return potentialA;
//                if (potentialA.getSteps() > 0){
//                    Point potentialNextPoint = new Point(this.currentLocation, potentialA.getDirection());
//                    LawnState s = checkLocation(potentialNextPoint);
//                    if(s == LawnState.empty || s == LawnState.energy || s == LawnState.grass){
//                        return potentialA;
//                    }
//                }
            }

            //check current direction
            Point nextPoint = new Point(currentLocation, direction);
            LawnState lawnState = checkLocation(nextPoint);
            if (lawnState == null) {
                return new Action(ActionState.scan);
            }
            switch (lawnState) {
                case grass:
                    return new Action(ActionState.move, 1, this.direction);
                case crater:
                case mower:
                case empty:
                case fence:
                case energy:
                case energymower:
                    return getNewDirection();
                default:
                    return getNewDirection();
            }
        }
    }


    public void performScan(String scanResults) {

        this.energy--;

        String[] values = scanResults.split(",");

        for (Direction d : Direction.values()) {
            String directionValue = values[d.getValue()];
            if (directionValue.contains("_")) {
                int otherMowerId = Integer.parseInt(directionValue.substring(directionValue.lastIndexOf(("_")) + 1));
                sharedLawn.addPoint(
                        this.id,
                        new Point(currentLocation, d),
                        new LawnSquareMower(LawnState.mower, otherMowerId, this.direction));  // direction is useless here
            } else {
                sharedLawn.addPoint(
                        this.id,
                        new Point(currentLocation, d),
                        new LawnSquare(LawnState.valueOf(values[d.getValue()])));
            }
        }
        checkforRecharge();
    }


    /**
     * Verify there is a square of fence
     * check bottom left has north and east fence
     * check top right has south and west fence
     */
    private boolean isLawnComplete() {
        // sort to get bottom left corner
        Point bottomLeft = getBottomLeftPoint();
        Point topRight = getTopRightPoint();

        if (bottomLeft.equals(topRight)) {
            return false;
        }


        LawnState north = checkLocation(new Point(bottomLeft, Direction.north));
        LawnState east = checkLocation(new Point(bottomLeft, Direction.east));

        LawnState south = checkLocation(new Point(topRight, Direction.south));
        LawnState west = checkLocation(new Point(topRight, Direction.west));

        LawnState bottom = checkLocation(bottomLeft);
        LawnState top = checkLocation(topRight);

        if (north != LawnState.fence ||
                east != LawnState.fence ||
                south != LawnState.fence ||
                west != LawnState.fence ||
                bottom != LawnState.fence ||
                top != LawnState.fence)
            return false;


        for (int x = bottomLeft.x; x < topRight.x; x++) {
            for (int y = bottomLeft.y; y < topRight.y; y++) {
                Point testPoint = new Point(x, y);
                LawnState testLawnState = checkLocation(testPoint);
                if (testLawnState == null || testLawnState == LawnState.grass) {
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
        //if (knownlawn.containsKey(p)) {
        if (sharedLawn.getLawn(this.id).containsKey(p)) {
            //return knownlawn.get(p);
            return sharedLawn.getLawn(this.id).get(p).getState();
        } else {
            return null;
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
                //this.direction = d;
                return new Action(ActionState.move, 0, d);
            }
        }

        // if all spaces around mower are scanned and empty
        //  find direction of grass or missing spaces
        //  shutdown if all spaces are found.

        if (surroundingSpacesScanned()) {
            Point nextPoint = getFirstGrassPoint();
            if (nextPoint == null) {
                nextPoint = getMissingAreaPoint();
                if (nextPoint == null) {
                    this.state = MowerState.off;
                    return new Action(ActionState.scan);
                } else {
                    return directionTowardPoint(nextPoint);
                }
            } else {
                return directionTowardPoint(nextPoint);
            }
        } else {
            return new Action(ActionState.scan);
        }

    }

    private Direction degreeToDirection(double degrees) {
        //north
        if (degrees <= (90 + (11.25 * 2)) && degrees > (90 - (11.25 * 2))) {    //112.5     67.5
            return Direction.north;
        }
        //northeast
        else if (degrees <= (45 + (11.25 * 2)) && degrees > (45 - (11.25 * 2))) {   //67.5  22.5
            return Direction.northeast;
        }
        //east
        else if (degrees <= (0 + (11.25 * 2)) && degrees > (0 - (11.25 * 2))) { //22.5  -22.5
            return Direction.east;
        }
        //southeast
        else if (degrees <= (-45 + (11.25 * 2)) && degrees > (-45 - (11.25 * 2))) { //-22.5 -67.5
            return Direction.southeast;
        }
        //south
        else if (degrees <= (-90 + (11.25 * 2)) && degrees > (-90 - (11.25 * 2))) { //-67.5 -112.5
            return Direction.south;
        }
        //southwest
        else if (degrees <= (-135 + (11.25 * 2)) && degrees > (-135 - (11.25 * 2))) {   //-112.5    -157.5
            return Direction.southwest;
        }
        //west
        //else if( degrees <= (180+(11.25*2)) && degrees > (180-(11.25*2)) ){ //202.5 157.5
        //    return Direction.west;
        //}
        else if (degrees <= 157.5 || degrees > 157.5) {
            return Direction.west;
        }
        //northwest
        else if (degrees <= (135 + (11.25 * 2)) && degrees > (135 - (11.25 * 2))) {  //157.5     112.5
            return Direction.northwest;
        }
        else{
            //System.out.println("ERROR: SHOULD NOT BE HERE LawnMower.degreeToDirection LAST ELSE");
            //System.out.println("mower_"+this.id+", degrees: " + degrees);
            //System.exit(1);
            return Direction.north;
        }
    }

    private Direction[] getAdjacentDirection(Direction d) {
        Direction[] dirs = new Direction[2];
        switch (d) {
            case north:
                dirs[0] = Direction.northwest;
                dirs[1] = Direction.northeast;
                return dirs;
            case northeast:
                dirs[0] = Direction.north;
                dirs[1] = Direction.east;
                return dirs;
            case east:
                dirs[0] = Direction.northeast;
                dirs[1] = Direction.southeast;
                return dirs;
            case southeast:
                dirs[0] = Direction.east;
                dirs[1] = Direction.south;
                return dirs;
            case south:
                dirs[0] = Direction.southeast;
                dirs[1] = Direction.southwest;
                return dirs;
            case southwest:
                dirs[0] = Direction.south;
                dirs[1] = Direction.west;
                return dirs;
            case west:
                dirs[0] = Direction.southwest;
                dirs[1] = Direction.northwest;
                return dirs;
            case northwest:
                dirs[0] = Direction.west;
                dirs[1] = Direction.north;
                return dirs;
            default:
                return null;
        }
    }

    private Direction checkNextAdjacentPoint(Direction nextD, Direction leftD, Direction rightD, int which, int count) {
        Point nextPoint;
        LawnState nextLawnState;
        if (count >= 8) {
            return null;
        }
        if (which == 0) {

            //check direct path
            nextPoint = new Point(currentLocation, nextD);
            nextLawnState = checkLocation(nextPoint);
            if (nextLawnState == LawnState.grass || nextLawnState == LawnState.empty || nextLawnState == LawnState.energy) {
                return nextD;
            } else {
                count++;
                return checkNextAdjacentPoint(null, leftD, rightD, 1, count);
            }
        } else if (which == 1) {
            //left
            nextPoint = new Point(currentLocation, leftD);
            nextLawnState = checkLocation(nextPoint);
            if (nextLawnState == LawnState.grass || nextLawnState == LawnState.empty || nextLawnState == LawnState.energy) {
                return leftD;
            } else {
                Direction[] ds = getAdjacentDirection(leftD);
                count++;
                return checkNextAdjacentPoint(null, ds[0], rightD, 2, count);
            }
        } else {
            //right
            nextPoint = new Point(currentLocation, rightD);
            nextLawnState = checkLocation(nextPoint);
            if (nextLawnState == LawnState.grass || nextLawnState == LawnState.empty || nextLawnState == LawnState.energy) {
                return rightD;
            } else {
                Direction[] ds = getAdjacentDirection(rightD);
                count++;
                return checkNextAdjacentPoint(null, leftD, ds[1], 1, count);
            }
        }

    }

    private Action directionTowardPoint(Point destPoint) {

        //System.out.println(currentLocation);
        //System.out.println(destPoint);

        double radians = Math.atan2(destPoint.y - currentLocation.y, destPoint.x - currentLocation.x);
        double degrees = Math.toDegrees(radians);

        Direction nextDirection = degreeToDirection(degrees);
        //Point nextPoint = new Point( currentLocation, nextDirection);
        //LawnState nextLawnState = checkLocation( nextPoint );
        Direction[] dirs = getAdjacentDirection(nextDirection);

        nextDirection = checkNextAdjacentPoint(nextDirection, dirs[0], dirs[1], 0, 0);

        if (nextDirection == null) {
            return new Action(ActionState.scan);
        }

        if (this.direction == nextDirection) {
            //Point nextPoint = new Point(currentLocation, nextDirection);
            //knownlawn.put(nextPoint, LawnState.empty);
            //sharedLawn.getLawn( this.id ).put(nextPoint, LawnState.empty);
            //currentLocation = nextPoint;
            return new Action(ActionState.move, 1, this.direction);
        } else {
            //this.direction = nextDirection;
            return new Action(ActionState.move, 0, nextDirection);
        }
    }

    /**
     * Scan map for missing area or grass
     */
    private Point getMissingAreaPoint() {
        Point bottomLeft = getBottomLeftPoint();
        Point topRight = getTopRightPoint();

        for (int x = bottomLeft.x; x < topRight.x; x++) {
            for (int y = bottomLeft.y; y < topRight.y; y++) {
                Point testPoint = new Point(x, y);
                LawnState testLawnState = checkLocation(testPoint);
                if (testLawnState == null || testLawnState == LawnState.grass) {
                    return testPoint;
                }
            }
        }
        return null;
    }

    private Point getBottomLeftPoint() {
        //LinkedHashMap<Point, LawnState> sorted = knownlawn.entrySet().stream()
        LinkedHashMap<Point, LawnSquare> sorted = sharedLawn.getLawn(this.id).entrySet().stream()
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
        //LinkedHashMap<Point, LawnState> sorted = knownlawn.entrySet().stream()
        LinkedHashMap<Point, LawnSquare> sorted = sharedLawn.getLawn(this.id).entrySet().stream()
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
        //HashMap<Point, LawnState> grassPoints = knownlawn.entrySet()
        HashMap<Point, LawnSquare> grassPoints = sharedLawn.getLawn(this.id).entrySet()
                .stream()
                .filter(map -> map.getValue().getState() == LawnState.grass)
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

    private Point getNearestEnergy() {
        //HashMap<Point, LawnState> grassPoints = knownlawn.entrySet()
        HashMap<Point, LawnSquare> energyPoints = sharedLawn.getLawn(this.id).entrySet()
                .stream()
                .filter(map -> (map.getValue().getState() == LawnState.energy || map.getValue().getState() == LawnState.energymower))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (prev, next) -> next, LinkedHashMap::new));


        if (energyPoints.size() == 1) {
            return energyPoints.entrySet().stream().findFirst().get().getKey();
        } else {
            Double minDistance = Double.MAX_VALUE;
            Point nearest = null;
            for (Point key : energyPoints.keySet()) {
                Double distance = Point2D.distance(currentLocation.x, currentLocation.y, key.x, key.y);
                if (distance < minDistance) {
                    nearest = key;
                    minDistance = distance;
                }
            }

            return nearest;
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

    private void printSelfLawn(){
        System.out.println("---- Mower "+id+" ----");
        Map<Point, LawnSquare> lawn = sharedLawn.getLawn(this.id);
        for (Point p : lawn.keySet()){
            System.out.println("["+p.x+","+p.y+"]: "+lawn.get(p).getState().toString());
        }
        System.out.println("--------------");
    }
}