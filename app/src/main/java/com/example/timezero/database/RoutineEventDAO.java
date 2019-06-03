package com.example.timezero.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.timezero.model.RoutineEvent;
import com.example.timezero.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RoutineEventDAO implements IRoutineEventDAO {

    private Context context;

    public RoutineEventDAO(Context context) {
        this.context = context;
    }

    @Override
    public void insertRoutineEvent(RoutineEvent routineEvent) {

        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        if (databaseHelper != null) {
            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
            try {
                long id = sqLiteDatabase.insert(
                        DatabaseScheme.TABLE_ROUTINE_EVENTS,
                        null,
                        entityToContentValues(routineEvent));
                routineEvent.setId(id);
            } finally {
                sqLiteDatabase.close();
            }
        }
    }

    @Override
    public void updateRoutineEvent(RoutineEvent routineEvent) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        if (databaseHelper != null) {
            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
            try {
                int numberOfUpdatedRows =
                        sqLiteDatabase.update(
                                DatabaseScheme.TABLE_ROUTINE_EVENTS,
                                entityToContentValues(routineEvent),
                                DatabaseScheme._ID + "=?",
                                new String[]{String.valueOf(routineEvent.getId())});
            } finally {
                sqLiteDatabase.close();
            }
        }
    }

    @Override
    public void deleteRoutineEvent(RoutineEvent routineEvent) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        if (databaseHelper != null) {
            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
            try {
                sqLiteDatabase.delete(
                        DatabaseScheme.TABLE_ROUTINE_EVENTS,
                        DatabaseScheme._ID + "=?",
                        new String[]{String.valueOf(routineEvent.getId())});
            } finally {
                sqLiteDatabase.close();
            }
        }
    }

    @Override
    public void deleteRoutineEventsForRoutine(long id) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        if (databaseHelper != null) {
            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
            try {
                sqLiteDatabase.delete(
                        DatabaseScheme.TABLE_ROUTINE_EVENTS,
                        DatabaseScheme.C_RE_ROUTINE_ID + "=?",
                        new String[]{String.valueOf(id)}
                );
            } finally {
                sqLiteDatabase.close();
            }
        }
    }


    @Override
    public RoutineEvent getRoutineEventByID(long id) {
        RoutineEvent routineEvent = null;
        DatabaseHelper databaseHelper =
                new DatabaseHelper(context);
        if (databaseHelper != null) {
            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
            Cursor cursor = sqLiteDatabase.query(
                    DatabaseScheme.TABLE_ROUTINE_EVENTS,
                    null,
                    DatabaseScheme._ID + "=?",
                    new String[]{String.valueOf(id)},
                    null,
                    null,
                    null);
            try {
                if (cursor != null && cursor.getCount() > 0) {
                    if (cursor.moveToNext()) {
                        routineEvent = cursorToEntity(cursor);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                sqLiteDatabase.close();
            }
        }
        return routineEvent;
    }

    @Override
    public long getRoutineId(long routineEventId) {
        long routineId = 0;
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        if (databaseHelper != null) {
            SQLiteDatabase sqLiteDatabase
                    = databaseHelper.getWritableDatabase();
            final String MY_QUERY = "SELECT * FROM "
                    + DatabaseScheme.TABLE_ROUTINE_EVENTS
                    + " WHERE " + DatabaseScheme._ID + "=?"
                    + " LIMIT 1;";
            Cursor cursor = sqLiteDatabase.rawQuery(MY_QUERY,
                    new String[]{String.valueOf(routineEventId)});
            try {
                if (cursor != null && cursor.getCount() > 0) {
                    routineId = cursorToEntity(cursor).getRoutineId();
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                sqLiteDatabase.close();
            }
        }
        return routineId;
    }


    @Override
    public List<RoutineEvent> getRoutineEventsByDate(int dayOfWeek) {
        List<RoutineEvent> routineEventList = new ArrayList<>();
        DatabaseHelper databaseHelper =
                new DatabaseHelper(context);
        if (databaseHelper != null) {
            SQLiteDatabase sqLiteDatabase
                    = databaseHelper.getWritableDatabase();

//            SELECT * FROM routine_events
//            INNER JOIN routines ON routine_events.routine_id=routines.routine_id
//            INNER JOIN day_of_week ON routine_events.id=day_of_week.routine_event_id
//            WHERE routine_events.routine_id=?
//            AND day_of_week.day=?
//            ORDER BY routine_events.start_date;
            final String MY_QUERY = "SELECT " + DatabaseScheme.TABLE_ROUTINE_EVENTS + ".* FROM "
                    + DatabaseScheme.TABLE_ROUTINE_EVENTS
                    + " JOIN " + DatabaseScheme.TABLE_ROUTINES
                    + " ON " + DatabaseScheme.TABLE_ROUTINE_EVENTS
                    + "." + DatabaseScheme.C_RE_ROUTINE_ID
                    + "=" + DatabaseScheme.TABLE_ROUTINES
                    + "." + DatabaseScheme._ID
                    + " JOIN " + DatabaseScheme.TABLE_DAY_OF_WEEK
                    + " ON " + DatabaseScheme.TABLE_ROUTINE_EVENTS
                    + "." + DatabaseScheme._ID
                    + "=" + DatabaseScheme.TABLE_DAY_OF_WEEK
                    + "." + DatabaseScheme.C_DAY_OF_WEEK_RE_ID
                    + " WHERE "
                    + DatabaseScheme.TABLE_ROUTINES
                    + "." + DatabaseScheme.C_R_STATUS + "=1"
                    + " AND " + DatabaseScheme.TABLE_DAY_OF_WEEK
                    + "." + DatabaseScheme.C_DAY_OF_WEEK + "=?"
                    + " ORDER BY " + DatabaseScheme.C_RE_START;
            Cursor cursor = sqLiteDatabase.rawQuery(MY_QUERY,
                    new String[]{String.valueOf(dayOfWeek)});
            try {
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        routineEventList.add(cursorToEntity(cursor));
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                sqLiteDatabase.close();
            }
        }
        return routineEventList;
    }

    @Override
    public List<RoutineEvent> getRoutineEventsForRoutine(long routineId, int dayOfWeek) {
        List<RoutineEvent> routineEventList = new ArrayList<>();
        DatabaseHelper databaseHelper =
                new DatabaseHelper(context);
        if (databaseHelper != null) {
            SQLiteDatabase sqLiteDatabase
                    = databaseHelper.getWritableDatabase();
            final String MY_QUERY = "SELECT " + DatabaseScheme.TABLE_ROUTINE_EVENTS + ".* FROM "
                    + DatabaseScheme.TABLE_ROUTINE_EVENTS
                    + " JOIN " + DatabaseScheme.TABLE_ROUTINES + " ON "
                    + DatabaseScheme.TABLE_ROUTINES + "." + DatabaseScheme._ID
                    + "=" + DatabaseScheme.TABLE_ROUTINE_EVENTS + "." + DatabaseScheme.C_RE_ROUTINE_ID
                    + " JOIN " + DatabaseScheme.TABLE_DAY_OF_WEEK + " ON "
                    + DatabaseScheme.TABLE_DAY_OF_WEEK + "." + DatabaseScheme.C_DAY_OF_WEEK_RE_ID
                    + "=" + DatabaseScheme.TABLE_ROUTINE_EVENTS + "." + DatabaseScheme._ID
                    + " WHERE " + DatabaseScheme.TABLE_ROUTINE_EVENTS + "." + DatabaseScheme.C_RE_ROUTINE_ID
                    + "=" + routineId + " AND " + DatabaseScheme.TABLE_DAY_OF_WEEK + "."
                    + DatabaseScheme.C_DAY_OF_WEEK + "=" + dayOfWeek
                    + " ORDER BY " + DatabaseScheme.TABLE_ROUTINE_EVENTS + "." + DatabaseScheme.C_RE_START
                    + ";";
            Cursor cursor = sqLiteDatabase.rawQuery(MY_QUERY,
                    new String[]{});
            try {
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        routineEventList.add(cursorToEntity(cursor));
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                sqLiteDatabase.close();
            }
        }
        return routineEventList;
    }

    private ContentValues entityToContentValues(RoutineEvent routineEvent) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseScheme.C_RE_NAME,
                routineEvent.getTitle());
        contentValues.put(DatabaseScheme.C_RE_DESCRIPTION,
                routineEvent.getDescription());
        contentValues.put(DatabaseScheme.C_RE_START,
                DateUtil.getStringFromDate(routineEvent.getStartDate()));
        contentValues.put(DatabaseScheme.C_RE_END,
                DateUtil.getStringFromDate(routineEvent.getEndTime()));
        contentValues.put(DatabaseScheme.C_RE_ROUTINE_ID,
                routineEvent.getRoutineId());
        if (routineEvent.isNotificationAllowed()) {
            contentValues.put(DatabaseScheme.C_RE_NOTIFICATIONS, 1);
        } else {
            contentValues.put(DatabaseScheme.C_RE_NOTIFICATIONS, 0);
        }
        contentValues.put(DatabaseScheme.C_RE_NOTIFICATION_BEFORE,
                routineEvent.getNotificationBefore());
        contentValues.put(DatabaseScheme.C_RE_TYPE, "routine_event");
        return contentValues;
    }

    private RoutineEvent cursorToEntity(Cursor cursor) {
        RoutineEvent routineEvent = new RoutineEvent();
        int columnId = cursor.getColumnIndex(DatabaseScheme._ID);
        long id = cursor.getLong(columnId);
        routineEvent.setId(id);

        Date startTime =
                DateUtil.getDateFromString(
                        cursor.getString(
                                cursor.getColumnIndex(
                                        DatabaseScheme.C_RE_START)));
        Date endTime =
                DateUtil.getDateFromString(
                        cursor.getString(
                                cursor.getColumnIndex(
                                        DatabaseScheme.C_RE_END)));
        int notificationAllowed = cursor.getInt(
                cursor.getColumnIndex(
                        DatabaseScheme.C_RE_NOTIFICATIONS));

        routineEvent.setTitle(cursor.getString(
                cursor.getColumnIndex(DatabaseScheme.C_RE_NAME)));
        routineEvent.setDescription(cursor.getString(
                cursor.getColumnIndex(DatabaseScheme.C_RE_DESCRIPTION)));
        routineEvent.setRoutineId(
                cursor.getInt(
                        cursor.getColumnIndex(
                                DatabaseScheme.C_RE_ROUTINE_ID)));
        routineEvent.setStartDate(startTime);
        routineEvent.setEndTime(endTime);
        if (notificationAllowed == 1) {
            routineEvent.setNotificationAllowed(true);
        }
        routineEvent.setNotificationBefore(cursor.getString(
                cursor.getColumnIndex(
                        DatabaseScheme.C_RE_NOTIFICATION_BEFORE)));
        routineEvent.setType(cursor.getString(
                cursor.getColumnIndex(
                        DatabaseScheme.C_RE_TYPE)));
        return routineEvent;
    }
}
