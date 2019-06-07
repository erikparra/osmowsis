package com.osmowsis;

import com.osmowsis.*;

public class Lawn {
    private LawnSquare[][] lawn;

    public Lawn( int width, int height ){
        lawn = new LawnSquare[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                lawn[i][j] = new LawnSquare( LawnState.grass );
            }
        }
    }

    public void addMower( int x, int y, int id ){
        lawn[x][y] = new LawnSquareMower( LawnState.mower, id);
    }

    public void addCrater( int x, int y ){
        lawn[x][y] = new LawnSquare( LawnState.crater );
    }




    public void print(){
        // from top down
        for( int i = lawn.length -1; i >= 0; i-- ){
            for( int j = 0; j < lawn[0].length; j++ ){
                if( j == 0 ){
                    System.out.println();
                    System.out.print(" | ");
                }

                System.out.print( lawn[i][j].getState().toString().charAt(0) + " | " );
            }
        }
        System.out.println();
    }

}
