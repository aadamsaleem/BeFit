package com.aadam.befit;

/**
 * Created by aadam on 14/4/2017.
 */

public class Constants {

    public static final String DATABASE_NAME = "befit.db";

    //User Table Keys
    public static final String KEY_USER_TABLE = "USER_TAB";
    public static final String KEY_USERID = "ID";
    public static final String KEY_EMAIL = "EMAIL";
    public static final String KEY_PASSWORD = "PASSWORD";
    public static final String KEY_NAME = "NAME";
    public static final String KEY_GENDER = "GENDER";
    public static final String KEY_HEIGHT = "HEIGHT";
    public static final String KEY_TWITTER_ID = "TWITTER_ID";

    //Session Table Keys
    public static final String KEY_SESSION_TABLE = "SESSION_TAB";
    public static final String KEY_SESSION_ID = "SESSION_ID";
    public static final String KEY_SESSION_LENGTH = "LENGTH";
    public static final String KEY_SESSION_STEPS = "STEPS";
    public static final String KEY_SESSION_DISTANCE = "DISTANCE";
    public static final String KEY_SESSION_DATETIME = "DATETIME";

    //Query to create user table
    public static final String DATABASE_CREATE_USER_TABLE = "create table " + Constants.KEY_USER_TABLE
            + "( "
            + Constants.KEY_USERID + " integer primary key autoincrement, "
            + Constants.KEY_EMAIL + " text,"
            + Constants.KEY_PASSWORD + " text,"
            + Constants.KEY_NAME + " text,"
            + Constants.KEY_HEIGHT + " text,"
            + Constants.KEY_GENDER + " text,"
            + Constants.KEY_TWITTER_ID + " text"
            + "); ";

    //Query to create records table
    public static final String DATABASE_CREATE_RECORDS_TABLE = "create table " + Constants.KEY_SESSION_TABLE
            + "( "
            + Constants.KEY_SESSION_ID + " integer primary key autoincrement, "
            + Constants.KEY_USERID + " integer,"
            + Constants.KEY_SESSION_LENGTH + " text,"
            + Constants.KEY_SESSION_STEPS + " text,"
            + Constants.KEY_SESSION_DISTANCE + " text,"
            + Constants.KEY_SESSION_DATETIME + " text,"
            + " FOREIGN KEY (" + Constants.KEY_USERID + ") REFERENCES " + Constants.KEY_USER_TABLE + "(" + Constants.KEY_USERID + "));";
}
