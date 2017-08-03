package com.example.drogomierz.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.drogomierz.data.DatabaseContract.*;

/**
 * Created by Mateusz on 01.08.2017.
 */

public class DatabaseDbHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "odometer.db";

    private static final int DATABASE_VERSION = 1;


    public DatabaseDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_ODOMETER_TABLE = "CREATE TABLE " + DatabaseEntry.TABLE_NAME + " (" +
                DatabaseEntry._ID + " INTEGER PRIMARY AUTOINCREMENT," +
                DatabaseEntry.COLUMN_DISTANCE + " DOUBLE NOT NULL," +
                DatabaseEntry.COLUMN_STATUS + " INTEGER NOT NULL" +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_ODOMETER_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DatabaseEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
