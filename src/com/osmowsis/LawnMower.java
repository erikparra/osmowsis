package com.osmowsis;

import com.osmowsis.*;
import java.awt.*;
import java.util.HashMap;

public class LawnMower {

    private int id;
    private Direction direction;
    private Boolean isCrashed;
    private Point currentLocation;
    private HashMap<Point, LawnState> knownlawn = new HashMap<>();

    public LawnMower(int id, Direction dir) {
        this.id = id;
        this.direction = dir;
        this.isCrashed = false;
        this.currentLocation = new Point(0, 0);
        this.knownlawn.put(currentLocation, LawnState.empty);
    }
}