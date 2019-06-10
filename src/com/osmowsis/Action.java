package com.osmowsis;

public class Action {
    private ActionState state;
    private int steps;
    private Direction direction;


    // scan or unknown
    public Action(ActionState state){
        this.state = state;
        this.steps = 0;
        this.direction = null;
    }

    //move
    public Action(ActionState state, int steps, Direction direction){
        this.state = state;
        this.steps = steps;
        this.direction = direction;
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


    public String toString(){
        if( state == ActionState.scan ){
            return this.state.toString();
        }
        else{
            return this.state.toString() + "," + this.steps + "," + this.direction.toString();
        }
    }
}
