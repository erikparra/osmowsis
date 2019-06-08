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

    public void addMower( int x, int y, int id ){
        lawn[x][y] = new LawnSquareMower( LawnState.mower, id);
    }

    public void addCrater( int x, int y ){
        lawn[x][y] = new LawnSquare( LawnState.crater );
    }

    public void setNumOfGrass(){
        int count = 0;
        for( int i = 0; i < lawn.length; i++ ){
            for( int j = 0; j < lawn[0].length; j++ ){
                if( lawn[i][j].getState() == LawnState.grass )
                    count++;
            }
        }
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

        //north
        scanResults += lawn[loc.x][loc.y+1].getState().toString() + ",";

        //northeast
        scanResults += lawn[loc.x+1][loc.y+1].getState().toString() + ",";

        //east
        scanResults += lawn[loc.x+1][loc.y].getState().toString() + ",";

        //southeast
        scanResults += lawn[loc.x+1][loc.y-1].getState().toString() + ",";

        //south
        scanResults += lawn[loc.x][loc.y-1].getState().toString() + ",";

        //southwest
        scanResults += lawn[loc.x-1][loc.y-1].getState().toString() + ",";

        //west
        scanResults += lawn[loc.x-1][loc.y].getState().toString() + ",";

        //northwest
        scanResults += lawn[loc.x - 1][loc.y + 1].getState().toString();

        return scanResults;

    }

    /**
     *
     */
    public String mowerMove(LawnMower mower, Action action) {

        // get direction
        // check num spaces for collisoin
        // apply changes to model
        // return status of/crash
        //todo: check direction move for collision
        Point pos = findMower(mower);
        switch (mower.getDirection()) {
            case north:
                //todo: check step != mower,crater,fence
                for (int i = 1; i <= action.getSteps(); i++) {
                    Point nextPos = new Point( pos.x, pos.y + i);
                    LawnState nextState = checkLocation( nextPos );
                    if (nextState == LawnState.mower ||
                            nextState == LawnState.crater ||
                            nextState == LawnState.fence) {
                        return "crash";
                    } else {
                        lawn[pos.x][pos.y] = new LawnSquare(LawnState.empty);
                        lawn[nextPos.x][nextPos.y + i] = new LawnSquareMower(LawnState.mower, mower.getId());
                        pos = nextPos;
                    }
                }
                return "ok";
            case northeast:
                //todo: check step != mower,crater,fence
                for (int i = 1; i <= action.getSteps(); i++) {
                    Point nextPos = new Point( pos.x, pos.y + i);
                    LawnState nextState = checkLocation( nextPos );
                    if (nextState == LawnState.mower ||
                            nextState == LawnState.crater ||
                            nextState == LawnState.fence) {
                        return "crash";
                    } else {
                        lawn[pos.x][pos.y] = new LawnSquare(LawnState.empty);
                        lawn[nextPos.x][nextPos.y + i] = new LawnSquareMower(LawnState.mower, mower.getId());
                        pos = nextPos;
                    }
                }
                return "ok";
            case east:
                for (int i = 1; i <= action.getSteps(); i++) {
                    //todo: check step != mower,crater,fence,
                    LawnState nextState = checkLocation(pos.x + i, pos.y);
                    if (nextState == LawnState.mower ||
                            nextState == LawnState.crater ||
                            nextState == LawnState.fence) {
                        return "crash";
                    } else {
                        lawn[pos.x][pos.y] = new LawnSquare(LawnState.empty);
                        lawn[pos.x + i][pos.y] = new LawnSquareMower(LawnState.mower, mower.getId());
                        pos = new Point(pos.x + i, pos.y);
                    }
                }
                return "ok";
            case southeast:
                for (int i = 1; i <= action.getSteps(); i++) {
                    //todo: check step != mower,crater,fence,
                    LawnState nextState = checkLocation(pos.x + i, pos.y - i);
                    if (nextState == LawnState.mower ||
                            nextState == LawnState.crater ||
                            nextState == LawnState.fence) {
                        return "crash";
                    } else {
                        lawn[pos.x][pos.y] = new LawnSquare(LawnState.empty);
                        lawn[pos.x + i][pos.y - i] = new LawnSquareMower(LawnState.mower, mower.getId());
                        pos = new Point(pos.x + i, pos.y - i);
                    }
                }
                return "ok";
            case south:
                for (int i = 1; i <= action.getSteps(); i++) {
                    //todo: check step != mower,crater,fence,
                    LawnState nextState = checkLocation(pos.x, pos.y - i);
                    if (nextState == LawnState.mower ||
                            nextState == LawnState.crater ||
                            nextState == LawnState.fence) {
                        return "crash";
                    } else {
                        lawn[pos.x][pos.y] = new LawnSquare(LawnState.empty);
                        lawn[pos.x][pos.y - i] = new LawnSquareMower(LawnState.mower, mower.getId());
                        pos = new Point(pos.x, pos.y - i);
                    }
                }
                return "ok";
            case southwest:
                for (int i = 1; i <= action.getSteps(); i++) {
                    //todo: check step != mower,crater,fence,
                    LawnState nextState = checkLocation(pos.x - i, pos.y - i);
                    if (nextState == LawnState.mower ||
                            nextState == LawnState.crater ||
                            nextState == LawnState.fence) {
                        return "crash";
                    } else {
                        lawn[pos.x][pos.y] = new LawnSquare(LawnState.empty);
                        lawn[pos.x - i][pos.y - i] = new LawnSquareMower(LawnState.mower, mower.getId());
                        pos = new Point(pos.x - i, pos.y - i);
                    }
                }
                return "ok";
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

    /**
     * return the state at the lawn location
     */
    private LawnState checkLocation( Point p ){
        return lawn[p.x][p.y].getState();
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
    }

}
