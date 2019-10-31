package com.osmowsis;

public class LawnSquareMower extends LawnSquare {

    private int id;
    private Direction direction;

    public LawnSquareMower ( LawnState ls, int id, Direction d ){
        super( ls );
        this.id = id;
        this.direction = d;
    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public Direction getDirection(){
        return this.direction;
    }

    public void setDirection( Direction d ){
        this.direction = d;
    }
}
