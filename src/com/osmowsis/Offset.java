package com.osmowsis;


public class Offset {
    private int xOffset;
    private int yOffset;

    public Offset(int xoff, int yoff){
        xOffset = xoff;
        yOffset = yoff;
    }

    public Point translate(Point p){
        return new Point(p.x + xOffset, p.y + yOffset);
    }
}
