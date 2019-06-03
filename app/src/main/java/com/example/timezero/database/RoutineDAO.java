package com.example.timezero.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.timezero.model.Routine;

import java.util.ArrayList;
import java.util.List;

public class RoutineDAO implements IRoutineDAO {

    private Context context;

    public RoutineDAO(Context context) {
        this.context = context;
    }

    @Override
    public void insertRoutine(Routine routine) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        if (databaseHelper != null) {
            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
            try {
                long id = sqLiteDatabase.insert(DatabaseScheme.TABLE_ROUTINES,
                        null,
                        entityToContentValues(routine));
                routine.setId(id);
            } finally {
                sqLiteDatabase.close();
            }
        }
    }

    @Override
    public void updateRoutine(Routine routine) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        if (databaseHelper != null) {
            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
            try {
                int numberOfUpdatedRows = sqLiteDatabase
                        .update(DatabaseScheme.TABLE_ROUTINES,
                                entityToContentValues(routine),
                                DatabaseScheme._ID + "=?",
                                new String[]{String.valueOf(routine.getId())});
            } finally {
                sqLiteDatabase.close();
            }
        }
    }

    @Override
    public void deleteRoutine(Routine routine) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        if (databaseHelper != null) {
            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
            try {
                sqLiteDatabase.delete(
                        DatabaseScheme.TABLE_ROUTINES,
                        DatabaseScheme._ID + "=?",
                        new String[]{String.valueOf(routine.getId())});
            } finally {
                sqLiteDatabase.close();
            }
        }
    }

    @Override
    public List<Routine> getAllRoutines() {
        List<Routine> routines = new ArrayList<>();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        if (databaseHelper != null) {
            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
            Cursor cursor = sqLiteDatabase.query(
                    DatabaseScheme.TABLE_ROUTINES,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
            try {
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        routines.add(cursorToEntity(cursor));
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                sqLiteDatabase.close();
            }
        }
        return routines;
    }

    @Override
    public Routine getRoutineByID(long id) {
        Routine routine = null;
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        if (databaseHelper != null) {
            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
            Cursor cursor = sqLiteDatabase.query(
                    DatabaseScheme.TABLE_ROUTINES,
                    null,
                    DatabaseScheme._ID + "=?",
                    new String[]{String.valueOf(id)},
                    null,
                    null,
                    null);
            try {
                if (cursor != null && cursor.getCount() > 0) {
                    if (cursor.moveToNext()) {
                        routine = cursorToEntity(cursor);
                    }
                }

            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                sqLiteDatabase.close();
            }
        }
        return routine;
    }

    private ContentValues entityToContentValues(Routine routine) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseScheme.C_R_TITLE,
                routine.getTitle());
        contentValues.put(DatabaseScheme.C_R_STATUS,
                routine.isActive());
        return contentValues;
    }

    private Routine cursorToEntity(Cursor cursor) {
        Routine routine = new Routine();
        routine.setId(cursor.getLong(
                cursor.getColumnIndex(DatabaseScheme._ID)));
        routine.setTitle(cursor.getString(
                cursor.getColumnIndex(DatabaseScheme.C_R_TITLE)));
        int active = cursor.getInt(
                cursor.getColumnIndex(DatabaseScheme.C_R_STATUS));
        if (active == 1) {
            routine.setActive(true);
        }
        return routine;
    }
}
