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

    public String toSimpleString(){
        if(this.toString().equalsIgnoreCase("north"))
            return "N";
        else if(this.toString().equalsIgnoreCase("northeast"))
            return "NE";
        else if(this.toString().equalsIgnoreCase("east"))
            return "E";
        else if(this.toString().equalsIgnoreCase("southeast"))
            return "SE";
        else if(this.toString().equalsIgnoreCase("south"))
            return "S";
        else if(this.toString().equalsIgnoreCase("southwest"))
            return "SW";
        else if(this.toString().equalsIgnoreCase("west"))
            return "W";
        else if(this.toString().equalsIgnoreCase("northwest"))
            return "NW";
        else
            return "X";
    }
}