package com.aadam.befit.models;

import java.util.Date;

/**
 * Created by aadam on 15/4/2017.
 */

public class Record {
    private int userID;
    private String sessionLength;
    private String sessionSteps;
    private String sessionDistance;
    private Date sessionDate;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getSessionLength() {
        return sessionLength;
    }

    public void setSessionLength(String sessionLength) {
        this.sessionLength = sessionLength;
    }

    public String getSessionSteps() {
        return sessionSteps;
    }

    public void setSessionSteps(String sessionSteps) {
        this.sessionSteps = sessionSteps;
    }

    public String getSessionDistance() {
        return sessionDistance;
    }

    public void setSessionDistance(String sessionDistance) {
        this.sessionDistance = sessionDistance;
    }

    public Date getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(Date sessionDate) {
        this.sessionDate = sessionDate;
    }
}
