package com.osmowsis;

public class LawnSquareMower extends LawnSquare {

    private int id;

    public LawnSquareMower ( LawnState ls, int id ){
        super( ls );
        this.id = id;
    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }
}
