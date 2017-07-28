package com.aadam.befit.models;

/**
 * Created by aadam on 16/4/2017.
 */

public class LeaderboardItem {

    private int userID;
    private String steps;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }
}
