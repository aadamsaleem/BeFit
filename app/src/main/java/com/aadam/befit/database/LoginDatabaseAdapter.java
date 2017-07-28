package com.aadam.befit.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.aadam.befit.Constants;
import com.aadam.befit.models.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by aadam on 14/4/2017.
 */

public class LoginDatabaseAdapter {

    //Properties
    private final Context context;
    public SQLiteDatabase db;
    private DatabaseHelper databaseHelper;

    //region Constructor
    public LoginDatabaseAdapter(Context context) {
        this.context = context;
        databaseHelper = new DatabaseHelper(this.context, Constants.DATABASE_NAME, null, 2);
    }
    //endregion

    //region Public Methods
    public LoginDatabaseAdapter open() throws SQLException {
        db = databaseHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        db.close();
    }

    public SQLiteDatabase getDatabaseInstance() {
        return db;
    }

    public void insertNewUser(String userEmail, String password) {
        ContentValues newValues = new ContentValues();

        newValues.put(Constants.KEY_EMAIL, userEmail);
        newValues.put(Constants.KEY_PASSWORD, password);

        db.insert(Constants.KEY_USER_TABLE, null, newValues);
    }

    public void insertNewTwitterUser(String userEmail, String twitterID, String  userName) {
        ContentValues newValues = new ContentValues();

        newValues.put(Constants.KEY_EMAIL, userEmail);
        newValues.put(Constants.KEY_TWITTER_ID, twitterID);
        newValues.put(Constants.KEY_NAME, userName);

        db.insert(Constants.KEY_USER_TABLE, null, newValues);
    }

    public boolean comparePassword(String userEmail, String testPassword) {
        Cursor cursor = db.query(Constants.KEY_USER_TABLE, null, Constants.KEY_EMAIL + "=?", new String[]{userEmail}, null, null, null);
        if (cursor.getCount() < 1)
        {
            cursor.close();
            return false;
        }
        cursor.moveToFirst();
        boolean result = testPassword.equals(cursor.getString(cursor.getColumnIndex(Constants.KEY_PASSWORD)));
        cursor.close();
        return result;
    }

    public int getUserID(String userEmail) {
        Cursor cursor = db.query(Constants.KEY_USER_TABLE, null, Constants.KEY_EMAIL + "=?", new String[]{userEmail}, null, null, null);
        if (cursor.getCount() < 1)
        {
            cursor.close();
            return -1;
        }
        cursor.moveToFirst();
        int id = cursor.getInt(cursor.getColumnIndex(Constants.KEY_USERID));
        cursor.close();
        return id;
    }

    public boolean checkUserExists(String email) {
        Cursor cursor = db.query(Constants.KEY_USER_TABLE, null, Constants.KEY_EMAIL + "=?", new String[]{email}, null, null, null);
        if (cursor.getCount() < 1)
        {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;

    }

    //to update user preferences
    public void updateEntry(String email, String name, String gender, String height, String twitterID) {

        ContentValues updatedValues = new ContentValues();

        updatedValues.put(Constants.KEY_EMAIL, email);
        updatedValues.put(Constants.KEY_NAME, name);
        updatedValues.put(Constants.KEY_HEIGHT, height);
        updatedValues.put(Constants.KEY_GENDER, gender);
        updatedValues.put(Constants.KEY_TWITTER_ID, twitterID);

        String where = Constants.KEY_EMAIL + "= ?";
        db.update(Constants.KEY_USER_TABLE, updatedValues, where, new String[]{email});
    }

    //returns all user details
    public User getUserByEmail(String email) throws NullPointerException {
        Cursor cursor = db.query(Constants.KEY_USER_TABLE, null, Constants.KEY_EMAIL + "=?", new String[]{email}, null, null, null);
        User user = new User();
        if(cursor.getCount()<1)
        {
            cursor.close();
            return null;
        }
        else {
            cursor.moveToFirst();
            user.setId(cursor.getInt(cursor.getColumnIndex(Constants.KEY_USERID)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(Constants.KEY_EMAIL)));
            user.setName(cursor.getString(cursor.getColumnIndex(Constants.KEY_NAME)));
            user.setGender(cursor.getString(cursor.getColumnIndex(Constants.KEY_GENDER)));
            user.setHeight(cursor.getString(cursor.getColumnIndex(Constants.KEY_HEIGHT)));
            user.setTwitterID(cursor.getString(cursor.getColumnIndex(Constants.KEY_TWITTER_ID)));

        }
        cursor.close();
        return user;

    }

    //returns username if ID is known
    public String getUserNameByID(int userID) throws NullPointerException {
        Cursor cursor = db.query(Constants.KEY_USER_TABLE, null, Constants.KEY_USERID + "=?", new String[]{String.valueOf(userID)}, null, null, null);
        if(cursor.getCount()<1)
        {
            return null;
        }
        cursor.moveToFirst();
        String username = cursor.getString(cursor.getColumnIndex(Constants.KEY_NAME));
        cursor.close();
        return username;

    }
    //endregion

}