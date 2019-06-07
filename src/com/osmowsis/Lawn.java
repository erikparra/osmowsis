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

    private void renderHorizontalBar(int size) {
        for (int k = 0; k < size; k++) {
            System.out.print("-");
        }
        System.out.println("");
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

}
