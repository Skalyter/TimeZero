package com.example.timezero.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.timezero.model.Reminder;
import com.example.timezero.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReminderDAO implements IReminderDAO {
    private Context context;

    public ReminderDAO(Context context) {
        this.context = context;
    }

    @Override
    public void insertReminder(Reminder reminder) {
        DatabaseHelper databaseHelper =
                new DatabaseHelper(context);
        if(databaseHelper != null) {
            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
            try {
                long id = sqLiteDatabase
                        .insert(
                                DatabaseScheme.TABLE_REMINDERS,
                                null,
                                entityToContentValues(reminder));
                reminder.setId(id);
            } finally {
                sqLiteDatabase.close();
            }
        }
    }

    @Override
    public void updateReminder(Reminder reminder) {
        DatabaseHelper databaseHelper =
                new DatabaseHelper(context);
        if (databaseHelper != null) {
            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
            try {
                int numberOfUpdatedRows =
                        sqLiteDatabase
                            .update(DatabaseScheme.TABLE_REMINDERS,
                                    entityToContentValues(reminder),
                                    DatabaseScheme._ID + "=? ",
                                    new String[] {String.valueOf(reminder.getId())});
            } finally {
                sqLiteDatabase.close();
            }
        }
    }

    @Override
    public void deleteReminder(Reminder reminder) {
        DatabaseHelper databaseHelper =
                new DatabaseHelper(context);
        if(databaseHelper != null){
            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
            try {
                sqLiteDatabase.delete(DatabaseScheme.TABLE_REMINDERS,
                        DatabaseScheme._ID + "=?",
                        new String[] {String.valueOf(reminder.getId())});
            } finally {
                sqLiteDatabase.close();
            }
        }
    }

    @Override
    public List<Reminder> getAllReminders() {
        List<Reminder> reminders = new ArrayList<>();
        DatabaseHelper databaseHelper =
                new DatabaseHelper(context);
        if(databaseHelper != null){
            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
            Cursor cursor = sqLiteDatabase
                    .query(DatabaseScheme.TABLE_REMINDERS,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null);
            try {
                if(cursor != null && cursor.getCount() > 0){
                    while (cursor.moveToNext()) {
                        reminders.add(cursorToEntity(cursor));
                    }
                }
            } finally {
                if(cursor != null){
                    cursor.close();
                }
                sqLiteDatabase.close();
            }
        }
        return reminders;
    }

    @Override
    public List<Reminder> getRemindersByDate(Date calendar) {
        List<Reminder> reminderList = new ArrayList<>();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        if(databaseHelper != null){
            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
            Cursor cursor = sqLiteDatabase.query(
                    DatabaseScheme.TABLE_REMINDERS,
                    null,
                    DatabaseScheme.C_REM_DATE + " LIKE "
                            + "\"%" + DateUtil.getStringDateFromDate(calendar) + "%\"",
                    null,
                    null,
                    null,
                    DatabaseScheme.C_REM_DATE);
            try {
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        reminderList.add(cursorToEntity(cursor));
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                sqLiteDatabase.close();
            }
        }
        return reminderList;
    }

    @Override
    public Reminder getReminderByID(long id) {
        Reminder reminder = null;
        DatabaseHelper databaseHelper =
            new DatabaseHelper(context);
        if(databaseHelper != null) {
            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
            Cursor cursor = sqLiteDatabase.query(
                    DatabaseScheme.TABLE_REMINDERS,
                    null,
                    DatabaseScheme._ID + "=?",
                    new String[] {String.valueOf(id)},
                    null,
                    null,
                    null);
            try {
                if(cursor!=null && cursor.getCount() > 0) {
                    if (cursor.moveToNext()) {
                        reminder = cursorToEntity(cursor);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                sqLiteDatabase.close();
            }
        }
        return reminder;
    }

    private ContentValues entityToContentValues(Reminder reminder){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseScheme.C_REM_NAME,
                reminder.getTitle());
        contentValues.put(DatabaseScheme.C_REM_DESCRIPTION,
                reminder.getDescription());
        contentValues.put(DatabaseScheme.C_REM_DATE,
                DateUtil.getStringFromDate(reminder.getStartDate()));
        if(reminder.isNotificationAllowed()){
            contentValues.put(DatabaseScheme.C_REM_NOTIFICATION, 1);
        } else {
            contentValues.put(DatabaseScheme.C_REM_NOTIFICATION, 0);
        }
        contentValues.put(DatabaseScheme.C_REM_NOTIFICATION_BEFORE,
                reminder.getNotificationBefore());
        contentValues.put(DatabaseScheme.C_REM_TYPE, "reminder");
        return contentValues;
    }

    private Reminder cursorToEntity (Cursor cursor){
        Reminder reminder = new Reminder();
        reminder.setId(cursor.getLong(
                cursor.getColumnIndex(DatabaseScheme._ID)));
        reminder.setTitle(cursor.getString(
                cursor.getColumnIndex(DatabaseScheme.C_REM_NAME)));
        reminder.setDescription(cursor.getString(
                cursor.getColumnIndex(DatabaseScheme.C_REM_DESCRIPTION)));

        Date startDateTime =
                DateUtil.getDateFromString(
                        cursor.getString(
                                cursor.getColumnIndex(
                                        DatabaseScheme.C_REM_DATE)));
        reminder.setStartDate(startDateTime);

        int notificationAllowed = cursor.getInt(
                cursor.getColumnIndex(
                        DatabaseScheme.C_REM_NOTIFICATION));
        if(notificationAllowed==1){
            reminder.setNotificationAllowed(true);
        }
        reminder.setNotificationBefore(cursor.getString(
                cursor.getColumnIndex(
                        DatabaseScheme.C_REM_NOTIFICATION_BEFORE)));
        reminder.setType(cursor.getString(
                cursor.getColumnIndex(
                        DatabaseScheme.C_REM_TYPE)));
        return reminder;
    }
}
