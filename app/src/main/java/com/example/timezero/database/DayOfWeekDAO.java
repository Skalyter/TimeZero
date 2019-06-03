package com.example.timezero.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.timezero.model.DayOfWeek;

import java.util.ArrayList;
import java.util.List;

public class DayOfWeekDAO implements IDayOfWeekDAO {
    Context context;

    public DayOfWeekDAO(Context context) {
        this.context = context;
    }

    @Override
    public void insertDayOfWeek(DayOfWeek dayOfWeek) {
        DatabaseHelper databaseHelper =
                new DatabaseHelper(context);
        if (databaseHelper != null) {
            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
            try{
                long id = sqLiteDatabase.insert(
                        DatabaseScheme.TABLE_DAY_OF_WEEK,
                        null,
                        entityToContentValues(dayOfWeek));
                dayOfWeek.setId(id);
            } finally {
                sqLiteDatabase.close();
            }
        }
    }

    @Override
    public void updateDayOfWeek(DayOfWeek dayOfWeek, int routineEventId) {
        DatabaseHelper databaseHelper =
                new DatabaseHelper(context);
        if (databaseHelper != null) {
            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
            try{
                int numberOfUpdatedRows =
                        sqLiteDatabase.update(
                                DatabaseScheme.TABLE_DAY_OF_WEEK,
                                entityToContentValues(dayOfWeek),
                                DatabaseScheme.C_DAY_OF_WEEK_RE_ID + "=?",
                                new String[] {String.valueOf(routineEventId)});
            } finally {
                sqLiteDatabase.close();
            }
        }
    }

    @Override
    public void deleteDayOfWeek(DayOfWeek dayOfWeek) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        if (databaseHelper != null) {
            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
            try {
                sqLiteDatabase.delete(
                        DatabaseScheme.TABLE_DAY_OF_WEEK,
                        DatabaseScheme.C_DAY_OF_WEEK_RE_ID + "=?",
                        new String[] {String.valueOf(dayOfWeek.getRoutineEventId())});
            } finally {
                sqLiteDatabase.close();
            }
        }
    }

    @Override
    public DayOfWeek getDayOfWeekById(long id) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        DayOfWeek dayOfWeek = new DayOfWeek();
        if (databaseHelper != null) {
            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
            Cursor cursor = sqLiteDatabase.query(
                    DatabaseScheme.TABLE_DAY_OF_WEEK,
                    null,
                    DatabaseScheme._ID + "=?",
                    new String[] {String.valueOf(id)},
                    null,
                    null,
                    null);
            try{
                if (cursor != null && cursor.getCount() > 0) {
                    dayOfWeek = cursorToEntity(cursor);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                sqLiteDatabase.close();
            }
        }
        return dayOfWeek;
    }

    @Override
    public List<DayOfWeek> getDaysOfWeekByRoutineEventId(long id) {
        List<DayOfWeek> daysOfWeek = new ArrayList<>();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        if (databaseHelper != null) {
            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
            Cursor cursor = sqLiteDatabase.query(
                    DatabaseScheme.TABLE_DAY_OF_WEEK,
                    null,
                    DatabaseScheme.C_DAY_OF_WEEK_RE_ID + "=?",
                    new String[] {String.valueOf(id)},
                    null,
                    null,
                    DatabaseScheme.C_DAY_OF_WEEK);
            try {
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        daysOfWeek.add(cursorToEntity(cursor));
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                sqLiteDatabase.close();
            }
        }
        return daysOfWeek;
    }

    private ContentValues entityToContentValues (DayOfWeek dayOfWeek){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseScheme.C_DAY_OF_WEEK,
                dayOfWeek.getNumberOfDay());
        contentValues.put(DatabaseScheme.C_DAY_OF_WEEK_RE_ID,
                dayOfWeek.getRoutineEventId());
        return contentValues;
    }

    private DayOfWeek cursorToEntity (Cursor cursor){
        DayOfWeek dayOfWeek = new DayOfWeek();

        dayOfWeek.setId(
                cursor.getLong(
                        cursor.getColumnIndex(DatabaseScheme._ID)));
        dayOfWeek.setNumberOfDay(
                cursor.getInt(
                        cursor.getColumnIndex(
                                DatabaseScheme.C_DAY_OF_WEEK)));
        dayOfWeek.setRoutineEventId(
                cursor.getInt(
                        cursor.getColumnIndex(
                                DatabaseScheme.C_DAY_OF_WEEK_RE_ID)));
        return dayOfWeek;
    }
}
