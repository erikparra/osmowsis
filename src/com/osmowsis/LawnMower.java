package com.osmowsis;

import com.osmowsis.*;
import java.awt.*;
import java.util.HashMap;

public class LawnMower {

    private Direction direction;
    private Boolean isCrashed;
    private Point currentLocation;
    private HashMap<Point, LawnState> lawn = new HashMap<>();

    public LawnMower(Direction dir) {
        direction = dir;
        isCrashed = false;
        currentLocation = new Point(0, 0);
        lawn.put(currentLocation, LawnState.EMPTY);
    }
}