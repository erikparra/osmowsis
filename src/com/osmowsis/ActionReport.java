package com.osmowsis;

public class ActionReport {
    public Action action;
    public int mowerId;
    public String response;

    public ActionReport(Action a, int mowerId, String response){
        this.action = a;
        this.mowerId = mowerId;
        if (this.action.getState() == ActionState.scan)
            this.response = "ok";
        else
            this.response = response;
    }

    public String toString(){
        String s = String.format("Mower %d: %s", mowerId, action.getState().toString());
        if (action.getState() == ActionState.move)
            s += (" for " + action.getSteps() + " steps");
        s += ("\nResponse: " + response);
        return s;
    }
}
