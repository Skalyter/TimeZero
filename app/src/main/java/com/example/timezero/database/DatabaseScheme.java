package com.example.timezero.database;

import android.provider.BaseColumns;


public interface DatabaseScheme extends BaseColumns {

    //Reminders table
    String TABLE_REMINDERS = "reminders";

    String C_REM_NAME = "name";
    String C_REM_DESCRIPTION = "description";
    String C_REM_DATE = "time";
    String C_REM_NOTIFICATION = "notification_allowed";
    String C_REM_NOTIFICATION_BEFORE = "notification_before";
    String C_REM_TYPE = "type";

    String CREATE_REMINDERS_TABLE =
            "CREATE TABLE "
                    + TABLE_REMINDERS + " ("
                    + _ID + " INTEGER PRIMARY KEY, "
                    + C_REM_NAME + " TEXT, "
                    + C_REM_DESCRIPTION + " TEXT, "
                    + C_REM_DATE + " TEXT, "
                    + C_REM_NOTIFICATION + " INTEGER, "
                    + C_REM_NOTIFICATION_BEFORE + " TEXT, "
                    + C_REM_TYPE + " TEXT"
                    + ")";

    String DELETE_REMINDERS_TABLE = "DROP TABLE IF EXISTS " + TABLE_REMINDERS;


    //Events table
    String TABLE_EVENTS = "events";

    String C_E_NAME = "name";
    String C_E_DESCRIPTION = "description";
    String C_E_CATEGORY = "category";
    String C_E_START = "start_date";
    String C_E_END = "end_date";
    String C_E_FINISHED = "finished";
    String C_E_NOTIFICATIONS = "notifications_allowed";
    String C_E_NOTIFICATIONS_BEFORE = "notification_before";
    String C_E_TYPE = "type";

    String CREATE_EVENTS_TABLE =
            "CREATE TABLE "
                    + TABLE_EVENTS + " ("
                    + _ID + " INTEGER PRIMARY KEY, "
                    + C_E_NAME + " TEXT, "
                    + C_E_DESCRIPTION + " TEXT, "
                    + C_E_CATEGORY + " TEXT, "
                    + C_E_START + " TEXT, "
                    + C_E_END + " TEXT, "
                    + C_E_FINISHED + " INTEGER, "
                    + C_E_NOTIFICATIONS + " INTEGER, "
                    + C_E_NOTIFICATIONS_BEFORE + " TEXT, "
                    + C_E_TYPE + " TEXT"
                    + ")";
    String DELETE_EVENTS_TABLE = "DROP TABLE IF EXISTS " + TABLE_EVENTS;


    //Routines table
    String TABLE_ROUTINES = "routines";

    String C_R_TITLE = "title";
    String C_R_STATUS = "status";

    String CREATE_ROUTINES_TABLE =
            "CREATE TABLE "
                    + TABLE_ROUTINES + " ("
                    + _ID + " INTEGER PRIMARY KEY, "
                    + C_R_TITLE + " TEXT, "
                    + C_R_STATUS + " INTEGER"
                    + ")";
    String DELETE_ROUTINES_TABLE = "DROP TABLE IF EXISTS " + TABLE_ROUTINES;


    //RoutineEvent table
    String TABLE_ROUTINE_EVENTS = "routine_events";

    String C_RE_NAME = "name";
    String C_RE_DESCRIPTION = "description";
    String C_RE_START = "start_time";
    String C_RE_END = "end_time";
    String C_RE_ROUTINE_ID = "routine_id";
    String C_RE_NOTIFICATIONS = "notifications_allowed";
    String C_RE_NOTIFICATION_BEFORE = "notification_before";
    String C_RE_TYPE = "type";

    String CREATE_ROUTINE_EVENTS_TABLE =
            "CREATE TABLE "
            + TABLE_ROUTINE_EVENTS + " ("
            + _ID + " INTEGER PRIMARY KEY, "
            + C_RE_NAME + " TEXT, "
            + C_RE_DESCRIPTION + " TEXT, "
            + C_RE_START + " TEXT, "
            + C_RE_END + " TEXT, "
            + C_RE_ROUTINE_ID + " INTEGER, "
            + C_RE_NOTIFICATIONS + " INTEGER, "
            + C_RE_NOTIFICATION_BEFORE + " TEXT, "
            + C_RE_TYPE + " TEXT, "
            + "FOREIGN KEY ("+ C_RE_ROUTINE_ID + ") REFERENCES "
            + TABLE_ROUTINES + "(" + _ID + "))";

    String DELETE_ROUTINE_EVENTS_TABLE = "DROP TABLE IF EXISTS " + TABLE_ROUTINE_EVENTS;


    //DayOfWeek table
    String TABLE_DAY_OF_WEEK = "days_of_week";

    String C_DAY_OF_WEEK = "day";
    String C_DAY_OF_WEEK_RE_ID = "routine_event_id";

    String CREATE_DAY_OF_WEEK_TABLE =
            "CREATE TABLE "
            + TABLE_DAY_OF_WEEK + " ("
            + _ID + " INTEGER, "
            + C_DAY_OF_WEEK + " INTEGER, "
            + C_DAY_OF_WEEK_RE_ID + " INTEGER,"
            + "FOREIGN KEY (" + C_DAY_OF_WEEK_RE_ID + ") REFERENCES "
            + TABLE_ROUTINE_EVENTS + "(" + _ID + "))";

    String DELETE_DAY_OF_WEEK_TABLE = "DROP TABLE IF EXISTS " + TABLE_DAY_OF_WEEK;
}
