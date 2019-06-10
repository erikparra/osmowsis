package com.osmowsis;

import java.lang.Comparable;

public class Point extends java.awt.Point {

    public Point( int x, int y){
        super(x, y);
    }

    public Point( Point p ){
        super(p);
    }

    public Point(Point p, Direction direction){
        super(p);
        switch (direction) {
            case north:
                //this.x = this.x;
                this.y = this.y+1;
                break;
            case northeast:
                this.x = this.x+1;
                this.y = this.y+1;
                break;
            case east:
                this.x = this.x+1;
                //this.y = this.y;
                break;
            case southeast:
                this.x = this.x+1;
                this.y = this.y-1;
                break;
            case south:
                //this.x = this.x;
                this.y = this.y-1;
                break;
            case southwest:
                this.x = this.x-1;
                this.y = this.y-1;
                break;
            case west:
                this.x = this.x-1;
                //this.y = this.y;
                break;
            case northwest:
                this.x = this.x-1;
                this.y = this.y+1;
                break;
            default:
                break;
        }
    }
}
