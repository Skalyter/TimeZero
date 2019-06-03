package com.example.timezero.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 11;
    private static final String NAME = "timezero_db";

    public DatabaseHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DatabaseScheme.CREATE_REMINDERS_TABLE);
        sqLiteDatabase.execSQL(DatabaseScheme.CREATE_EVENTS_TABLE);
        sqLiteDatabase.execSQL(DatabaseScheme.CREATE_ROUTINE_EVENTS_TABLE);
        sqLiteDatabase.execSQL(DatabaseScheme.CREATE_ROUTINES_TABLE);
        sqLiteDatabase.execSQL(DatabaseScheme.CREATE_DAY_OF_WEEK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(DatabaseScheme.DELETE_REMINDERS_TABLE);
        sqLiteDatabase.execSQL(DatabaseScheme.DELETE_EVENTS_TABLE);
        sqLiteDatabase.execSQL(DatabaseScheme.DELETE_ROUTINE_EVENTS_TABLE);
        sqLiteDatabase.execSQL(DatabaseScheme.DELETE_ROUTINES_TABLE);
        sqLiteDatabase.execSQL(DatabaseScheme.DELETE_DAY_OF_WEEK_TABLE);
        onCreate(sqLiteDatabase);
    }
}
