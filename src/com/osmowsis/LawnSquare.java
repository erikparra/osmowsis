package com.osmowsis;

public class LawnSquare {
    private LawnState state;

    public LawnSquare( LawnState ls){
        this.state = ls;
    }

    public LawnState getState(){
        return this.state;
    }

    public void setState( LawnState ls ){
        this.state = ls;
    }

}
