package com.osmowsis;

import com.osmowsis.*;

public class Lawn {
    private LawnSquare[][] lawn;
    private int width;
    private int height;
    private int numberOfGrass;

    public Lawn( int width, int height ){
        this.width = width;
        this.height = height;
        this.lawn = new LawnSquare[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                this.lawn[i][j] = new LawnSquare( LawnState.grass );
            }
        }
    }

    // ** FOR UI STUFF
    public LawnSquare[][] getState(){
        return lawn.clone();
    }
    // **

    public int getNumberOfSquares(){
        return this.width * this.height;
    }

    public int getNumberOfGrass(){
        return numberOfGrass;
    }

    public int getNumberOfGrassCut(){
        int count = 0;
        for( int i = 0; i < lawn.length; i++ ){
            for( int j = 0; j < lawn[0].length; j++ ){
                if( lawn[i][j].getState() == LawnState.grass )
                    count++;
            }
        }
        return this.numberOfGrass - count;
    }

    public void setMower( int x, int y, int id, Direction d ){
        //lawn[x][y] = new LawnSquareMower( LawnState.mower, id);
        lawn[x][y] = new LawnSquareMower( LawnState.energymower, id, d);
    }

    public void addCrater( int x, int y ){
        lawn[x][y] = new LawnSquare( LawnState.crater );
    }

    public void setNumOfGrass( int mowerCount ){
        int count = 0;
        for( int i = 0; i < lawn.length; i++ ){
            for( int j = 0; j < lawn[0].length; j++ ){
                if( lawn[i][j].getState() == LawnState.grass )
                    count++;
            }
        }
        //does the original mower location still contain grass? or just charging pad?
        //this.numberOfGrass = count+mowerCount;
        this.numberOfGrass = count;
    }



    public void renderLawn() {
        int charWidth = width * 4 + 3;

        // display the rows of the lawn from top to bottom
        for (int y = height - 1; y >= 0; y--) {
            renderHorizontalBar(charWidth);

            // display the Y-direction identifier
            System.out.print(y);

            // display the contents of each square on this row
            for (int x = 0; x < width; x++) {
                System.out.print(" | ");
                LawnState ls = lawn[x][y].getState();
                if( ls == LawnState.energymower ){
                    System.out.print( 'm' );
                }
                else{
                    System.out.print( lawn[x][y].getState().toString().charAt(0));
                }
            }
            System.out.println(" |");
        }
        renderHorizontalBar(charWidth);

        // display the column X-direction identifiers
        System.out.print("  ");
        for (int i = 0; i < width; i++) {
            System.out.print("| " + i + " ");
        }
        System.out.println("|");

    }

    /**
     * Find the position of the lawn mower
     * build the scan string
     */
    public String getScan( LawnMower mower ){
        String scanResults = "";
        Point loc = findMower( mower );

        //if scan outisde boundry, return fence state


        //north
        scanResults += checkLocationAsString( new Point(loc, Direction.north)) + ",";

        //northeast
        scanResults += checkLocationAsString( new Point(loc, Direction.northeast)) + ",";

        //east
        scanResults += checkLocationAsString( new Point(loc, Direction.east)) + ",";

        //southeast
        scanResults += checkLocationAsString( new Point(loc, Direction.southeast)) + ",";

        //south
        scanResults += checkLocationAsString( new Point(loc, Direction.south)) + ",";

        //southwest
        scanResults += checkLocationAsString( new Point(loc, Direction.southwest)) + ",";

        //west
        scanResults += checkLocationAsString( new Point(loc, Direction.west)) + ",";

        //northwest
        scanResults += checkLocationAsString( new Point(loc, Direction.northwest));

        return scanResults;

    }


    /**
     *
     */
    public String validateMove( LawnMower mower, Action action ){
        Point origPos = findMower( mower );
        LawnState origState = checkLocation(origPos);

        if( action.getSteps() >= 1 ){
            Point nextPos = new Point(origPos, action.getDirection());
            LawnState nextState = checkLocation(nextPos);

            switch( nextState ){
                case crater:
                case fence:
                    moveMower(mower, action);
                    return "crash";
                case energymower:
                case mower:
                    return "stall,"+action.getSteps();
                case grass:
                case empty:
                case energy:
                    moveMower( mower, action);
                    return "ok";
                default:
                    return "UNKNOWN";
            }
        }
        else{
            //rotate mower
            lawn[origPos.x][origPos.y] = new LawnSquareMower(origState, mower.getId(), action.getDirection());
            return "ok";
        }
    }


    public void moveMower( LawnMower mower, Action action ){
        Point origPos = findMower( mower );
        LawnState origState = checkLocation(origPos);

        Point nextPos = new Point(origPos, action.getDirection());
        LawnState nextState = checkLocation(nextPos);

        if( origState == LawnState.energymower ){
            lawn[origPos.x][origPos.y] = new LawnSquare( LawnState.energy );
        }
        else{
            lawn[origPos.x][origPos.y] = new LawnSquare( LawnState.empty );
        }

        switch( nextState ){
            case crater:
                break;
            case fence:
                break;
            //case energyMower:  //will never be called for these values
            //case mower:
            case grass:
            case empty:
                lawn[nextPos.x][nextPos.y] = new LawnSquareMower( LawnState.mower, mower.getId(), mower.getDirection() );
                break;
            case energy:
                lawn[nextPos.x][nextPos.y] = new LawnSquareMower( LawnState.energymower, mower.getId(), mower.getDirection() );
                break;
            default:
                return;
        }
    }

    /**

    public String moveMower_OLD(LawnMower mower, Action action) {
        Point originalPos = findMower(mower);
        LawnState originalState = lawn[originalPos.x][originalPos.y].getState();
        Point currentPos = originalPos;
        switch (mower.getDirection()) {
            case north:
                for (int i = 1; i <= action.getSteps(); i++) {
                    Point nextPos = new Point(currentPos.x, currentPos.y + i);
                    LawnState nextState = checkLocation(nextPos);
                    if (nextState == LawnState.mower ||
                            nextState == LawnState.crater ||
                            nextState == LawnState.fence) {
                        return "crash";
                    } else {
                        lawn[currentPos.x][currentPos.y] = new LawnSquare(LawnState.empty);
                        lawn[nextPos.x][nextPos.y] = new LawnSquareMower(LawnState.mower, mower.getId());
                        currentPos = nextPos;
                    }
                }
                return "ok";
            case northeast:
                for (int i = 1; i <= action.getSteps(); i++) {
                    Point nextPos = new Point(currentPos.x + i, currentPos.y + i);
                    LawnState nextState = checkLocation(nextPos);
                    if (nextState == LawnState.mower ||
                            nextState == LawnState.crater ||
                            nextState == LawnState.fence) {
                        return "crash";
                    } else {
                        lawn[currentPos.x][currentPos.y] = new LawnSquare(LawnState.empty);
                        lawn[nextPos.x][nextPos.y] = new LawnSquareMower(LawnState.mower, mower.getId());
                        currentPos = nextPos;
                    }
                }
                return "ok";
            case east:
                for (int i = 1; i <= action.getSteps(); i++) {
                    Point nextPos = new Point(currentPos.x + i, currentPos.y);
                    LawnState nextState = checkLocation(nextPos);
                    if (nextState == LawnState.mower ||
                            nextState == LawnState.crater ||
                            nextState == LawnState.fence) {
                        return "crash";
                    } else {
                        lawn[currentPos.x][currentPos.y] = new LawnSquare(LawnState.empty);
                        lawn[nextPos.x][nextPos.y] = new LawnSquareMower(LawnState.mower, mower.getId());
                        currentPos = nextPos;
                    }
                }
                return "ok";
            case southeast:
                for (int i = 1; i <= action.getSteps(); i++) {
                    Point nextPos = new Point(currentPos.x + 1, currentPos.y - i);
                    LawnState nextState = checkLocation(nextPos);
                    if (nextState == LawnState.mower ||
                            nextState == LawnState.crater ||
                            nextState == LawnState.fence) {
                        return "crash";
                    } else {
                        lawn[currentPos.x][currentPos.y] = new LawnSquare(LawnState.empty);
                        lawn[nextPos.x][nextPos.y] = new LawnSquareMower(LawnState.mower, mower.getId());
                        currentPos = nextPos;
                    }
                }
                return "ok";
            case south:
                for (int i = 1; i <= action.getSteps(); i++) {
                    Point nextPos = new Point(currentPos.x, currentPos.y - i);
                    LawnState nextState = checkLocation(nextPos);
                    if (nextState == LawnState.mower ||
                            nextState == LawnState.crater ||
                            nextState == LawnState.fence) {
                        return "crash";
                    } else {
                        lawn[currentPos.x][currentPos.y] = new LawnSquare(LawnState.empty);
                        lawn[nextPos.x][nextPos.y] = new LawnSquareMower(LawnState.mower, mower.getId());
                        currentPos = nextPos;
                    }
                }
                return "ok";
            case southwest:
                for (int i = 1; i <= action.getSteps(); i++) {
                    Point nextPos = new Point(currentPos.x - i, currentPos.y - i);
                    LawnState nextState = checkLocation(nextPos);
                    if (nextState == LawnState.mower ||
                            nextState == LawnState.crater ||
                            nextState == LawnState.fence) {
                        return "crash";
                    } else {
                        lawn[currentPos.x][currentPos.y] = new LawnSquare(LawnState.empty);
                        lawn[nextPos.x][nextPos.y] = new LawnSquareMower(LawnState.mower, mower.getId());
                        currentPos = nextPos;
                    }
                }
                return "ok";
            case west:
                for (int i = 1; i <= action.getSteps(); i++) {
                    Point nextPos = new Point(currentPos.x - i, currentPos.y);
                    LawnState nextState = checkLocation(nextPos);
                    if (nextState == LawnState.mower ||
                            nextState == LawnState.crater ||
                            nextState == LawnState.fence) {
                        return "crash";
                    } else {
                        lawn[currentPos.x][currentPos.y] = new LawnSquare(LawnState.empty);
                        lawn[nextPos.x][nextPos.y] = new LawnSquareMower(LawnState.mower, mower.getId());
                        currentPos = nextPos;
                    }
                }
                return "ok";
            case northwest:
                for (int i = 1; i <= action.getSteps(); i++) {
                    Point nextPos = new Point(currentPos.x - i, currentPos.y + i);
                    LawnState nextState = checkLocation(nextPos);
                    if (nextState == LawnState.mower ||
                            nextState == LawnState.crater ||
                            nextState == LawnState.fence) {
                        return "crash";
                    } else {
                        lawn[currentPos.x][currentPos.y] = new LawnSquare(LawnState.empty);
                        lawn[nextPos.x][nextPos.y] = new LawnSquareMower(LawnState.mower, mower.getId());
                        currentPos = nextPos;
                    }
                }
                return "ok";
            default:
                return "Lawn - moveMower() - switch default";
        }
    }
    */


    /**
     * return the state at the lawn location
     */
    private LawnState checkLocation( Point p ){
        if( p.x >= this.width || p.x < 0 ){
            return LawnState.fence;
        }
        else if( p.y >= this.height ||p.y < 0 ){
            return LawnState.fence;
        }
        else{
            return lawn[p.x][p.y].getState();
        }
    }

    private String checkLocationAsString( Point p ){
        LawnState ls = checkLocation( p );
        if( ls == LawnState.mower || ls == LawnState.energymower ){
            LawnSquareMower lsm = (LawnSquareMower) lawn[p.x][p.y];
            return "mower_"+lsm.getId();
        }
        else{
            return ls.toString();
        }
    }

    private void renderHorizontalBar(int size) {
        for (int k = 0; k < size; k++) {
            System.out.print("-");
        }
        System.out.println("");
    }

    /**
     * find the current location of the mower on the lawn
     * @return Point location of mower
     */
    public Point findMower( LawnMower mower){
        for( int x = 0; x < lawn.length; x++ ){
            for( int y = 0; y < lawn[0].length; y++ ){
                if( lawn[x][y].getState() == LawnState.mower || lawn[x][y].getState() == LawnState.energymower ){
                    LawnSquareMower mowerSquare = (LawnSquareMower) lawn[x][y];
                    if( mowerSquare.getId() == mower.getId() ){
                        return new Point(x, y);
                    }
                }
            }
        }
        return null;
    }
}
