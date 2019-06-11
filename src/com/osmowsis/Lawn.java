package com.osmowsis;

import com.osmowsis.*;

import java.awt.*;

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

    public void addMower( int x, int y, int id ){
        lawn[x][y] = new LawnSquareMower( LawnState.mower, id);
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
        this.numberOfGrass = count+mowerCount;
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

                System.out.print( lawn[x][y].getState().toString().charAt(0));
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

        //todo: if scan outisde boundry, return fence state


        //north
        scanResults += checkLocation( new Point(loc, Direction.north)).toString() + ",";

        //northeast
        scanResults += checkLocation( new Point(loc, Direction.northeast)).toString() + ",";

        //east
        scanResults += checkLocation( new Point(loc, Direction.east)).toString() + ",";

        //southeast
        scanResults += checkLocation( new Point(loc, Direction.southeast)).toString() + ",";

        //south
        scanResults += checkLocation( new Point(loc, Direction.south)).toString() + ",";

        //southwest
        scanResults += checkLocation( new Point(loc, Direction.southwest)).toString() + ",";

        //west
        scanResults += checkLocation( new Point(loc, Direction.west)).toString() + ",";

        //northwest
        scanResults += checkLocation( new Point(loc, Direction.northwest)).toString();

        return scanResults;

    }

    /**
     *
     */
    public String moveMower(LawnMower mower, Action action) {
        //todo: check direction move for collision
        Point originalPos = findMower(mower);
        Point currentPos = originalPos;
        switch (mower.getDirection()) {
            case north:
                //todo: check step != mower,crater,fence
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
                //todo: check step != mower,crater,fence
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
                //todo: check step != mower,crater,fence
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
                //todo: check step != mower,crater,fence
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
                //todo: check step != mower,crater,fence
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
                //todo: check step != mower,crater,fence
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
                //todo: check step != mower,crater,fence
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
                //todo: check step != mower,crater,fence
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
    private Point findMower( LawnMower mower){
        for( int x = 0; x < lawn.length; x++ ){
            for( int y = 0; y < lawn[0].length; y++ ){
                if( lawn[x][y].getState() == LawnState.mower ){
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
