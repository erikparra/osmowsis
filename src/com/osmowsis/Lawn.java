package com.osmowsis;

import com.osmowsis.*;

public class Lawn {
    private LawnState[][] lawn;

    public Lawn( int width, int height ){
        lawn = new LawnState[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                lawn[i][j] = LawnState.grass;
            }
        }
    }
}
