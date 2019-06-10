package com.osmowsis;

public enum Direction {
    north(0),
    northeast(1),
    east(2),
    southeast(3),
    south(4),
    southwest(5),
    west(6),
    northwest(7);

    private final int value;

    private Direction( int value ){
        this.value = value;
    }

    public int getValue(){
        return this.value;
    }
}