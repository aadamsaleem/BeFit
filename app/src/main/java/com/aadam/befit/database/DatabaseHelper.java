package com.aadam.befit.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.aadam.befit.Constants;

/**
 * Created by aadam on 14/4/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    //region Constructor
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    //endregion

    //region Override Methods
    @Override
    public void onCreate(SQLiteDatabase db) {
        //create user table
        db.execSQL(Constants.DATABASE_CREATE_USER_TABLE);

        //create session records table
        db.execSQL(Constants.DATABASE_CREATE_RECORDS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {

        _db.execSQL("DROP TABLE IF EXISTS " + "TEMPLATE");
        onCreate(_db);
    }
    //endregion

}