package com.osmowsis;

public class Action {
    private ActionState state;
    private int steps;
    private Direction direction;

    private LawnState lawnState;

    //scan
    public Action(ActionState state){
        this.state = state;
        this.steps = 0;
        this.direction = null;
        this.lawnState = null;
    }

    //move
    public Action(ActionState state, int steps, Direction direction){
        this.state = state;
        this.steps = steps;
        this.direction = direction;
        this.lawnState = null;
    }

    //crash
    public Action(ActionState state, LawnState ls){
        this.state = state;
        this.steps = 0;
        this.direction = null;
        this.lawnState = ls;
    }

    public ActionState getState(){
        return this.state;
    }

    public int getSteps(){
        return this.steps;
    }

    public Direction getDirection(){
        return this.direction;
    }

    public LawnState getLawnState(){
        return this.lawnState;
    }

    public String toString(){
        if( state == ActionState.scan ){
            return this.state.toString();
        }
        else{
            return this.state.toString() + "," + this.steps + "," + this.direction.toString();
        }
    }
}
