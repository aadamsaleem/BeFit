package com.aadam.befit.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.aadam.befit.Constants;
import com.aadam.befit.models.LeaderboardItem;
import com.aadam.befit.models.Record;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by aadam on 14/4/2017.
 */

public class RecordsDatabaseAdapter {

    //Properties
    private final Context context;
    private SQLiteDatabase db;
    private DatabaseHelper databaseHelper;

    //region Constructor
    public RecordsDatabaseAdapter(Context context) {
        this.context = context;
        databaseHelper = new DatabaseHelper(this.context, Constants.DATABASE_NAME, null, 2);
    }
    //endregion

    public RecordsDatabaseAdapter open() throws SQLException {
        db = databaseHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        db.close();
    }

    //region Public Methods
    public void insertSession(Record record) {
        ContentValues newValues = new ContentValues();

        newValues.put(Constants.KEY_USERID, record.getUserID());
        newValues.put(Constants.KEY_SESSION_LENGTH, record.getSessionLength());
        newValues.put(Constants.KEY_SESSION_STEPS, record.getSessionSteps());
        newValues.put(Constants.KEY_SESSION_DISTANCE, record.getSessionDistance());
        newValues.put(Constants.KEY_SESSION_DATETIME, getDateTime());

        db.insert(Constants.KEY_SESSION_TABLE, null, newValues);
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public List<Record> getUserRecords(int userID){
        List<Record> recordsList = new ArrayList<>();

        Cursor cursor = db.query(Constants.KEY_SESSION_TABLE, null, Constants.KEY_USERID + " =?", new String[]{userID+""},null, null, Constants.KEY_SESSION_DATETIME + " DESC");
        while(cursor.moveToNext()){
            Record record = new Record();
            record.setUserID(userID);
            record.setSessionDate(stringToDate(cursor.getString(cursor.getColumnIndex(Constants.KEY_SESSION_DATETIME))));
            record.setSessionDistance(cursor.getString(cursor.getColumnIndex(Constants.KEY_SESSION_DISTANCE)));
            record.setSessionLength(cursor.getString(cursor.getColumnIndex(Constants.KEY_SESSION_LENGTH)));
            record.setSessionSteps(cursor.getString(cursor.getColumnIndex(Constants.KEY_SESSION_STEPS)));

            recordsList.add(record);

        }
        cursor.close();
        return  recordsList;
    }

    public List<LeaderboardItem> getLeaderboard(){
        List<LeaderboardItem> leaderboardList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT "+ Constants.KEY_USERID +", " + "SUM ("+ Constants.KEY_SESSION_STEPS+") AS STEPSUM " + "FROM "+ Constants.KEY_SESSION_TABLE +"  \n" + "GROUP BY "+ Constants.KEY_USERID+" ORDER BY STEPSUM DESC ; ", null);
        while(cursor.moveToNext()){
            LeaderboardItem item = new LeaderboardItem();
            item.setUserID(cursor.getInt(cursor.getColumnIndex(Constants.KEY_USERID)));
            item.setSteps(cursor.getString(1));

            leaderboardList.add(item);

        }
        cursor.close();
        return  leaderboardList;
    }

    private Date stringToDate(String dateString){
        Date date = null;
        SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = format.parse(dateString);
            System.out.println(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }
    //endregion
}
