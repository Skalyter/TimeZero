package com.example.timezero.database;

import android.app.Notification;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;

import com.example.timezero.R;
import com.example.timezero.model.Event;
import com.example.timezero.routines.AddEditRoutineEventActivity;
import com.example.timezero.util.DateUtil;
import com.example.timezero.util.NotificationJobService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.goncalves.pugnotification.notification.Load;
import br.com.goncalves.pugnotification.notification.PugNotification;

public class EventDAO implements IEventDAO {

    private Context context;

    public EventDAO(Context context){
        this.context=context;
    }

    @Override
    public void insertEvent(Event event) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        if(databaseHelper != null){
            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
            try{
                long id = sqLiteDatabase
                        .insert(
                                DatabaseScheme.TABLE_EVENTS,
                                null,
                                entityToContentValues(event));
                event.setId(id);

//                //pugNotification experiment
//                try {
////                    Thread.sleep(DateUtil.getMilisTillEvent(event));
//                    Load mLoad = PugNotification.with(context)
//                            .load()
//                            .title(event.getTitle())
//                            .message(event.getDescription())
//                            .autoCancel(true)
//                            .flags(Notification.DEFAULT_ALL)
//                            .smallIcon(R.drawable.ic_stat_timeo)
//                            .largeIcon(R.drawable.ic_timeo);
//                    mLoad.simple().build();
//                    //todo PugNotification onClick event(start app and open details activity)
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                //todo delete if doesn't work

                //setNotification(event);
//                ComponentName componentName = new ComponentName(context, NotificationJobService.class);
//                JobInfo info = new JobInfo.Builder(event.getId().intValue(), componentName)
//                        .setPersisted(true)
//                        .build();
//                JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
//                jobScheduler.schedule(info);
                //JobParameters jobParameters = new JobParameters();
                //NotificationJobService notificationJobService = new NotificationJobService(context, event);
                //notificationJobService.onStartJob(jobParameters)
            } finally {
                sqLiteDatabase.close();
            }
        }
    }

    @Override
    public void updateEvent(Event event) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        if(databaseHelper != null) {
            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
            try {
                int numberOfUpdatedRows =
                        sqLiteDatabase
                                .update(DatabaseScheme.TABLE_EVENTS,
                                        entityToContentValues(event),
                                        DatabaseScheme._ID + "=? ",
                                        new String[] {String.valueOf(event.getId())});
            } finally {
                sqLiteDatabase.close();
            }
        }
    }

    @Override
    public void deleteEvent(Event event) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        if(databaseHelper != null){
            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
            try{
                sqLiteDatabase
                        .delete(DatabaseScheme.TABLE_EVENTS,
                                DatabaseScheme._ID + "=?",
                                new String[] {String.valueOf(event.getId())});

                JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                scheduler.cancel(event.getId().intValue());
            } finally {
                sqLiteDatabase.close();
            }
        }
    }

    @Override
    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        if(databaseHelper != null){
            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
            Cursor cursor =sqLiteDatabase
                    .query(DatabaseScheme.TABLE_EVENTS,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null);
            try{
                if(cursor != null && cursor.getCount()>0){
                    while(cursor.moveToNext()){
                        events.add(cursorToEntity(cursor));
                    }
                }
            } finally {
                if(cursor != null){
                    cursor.close();
                }
                sqLiteDatabase.close();
            }
        }
        return events;
    }

    @Override
    public Event getEventByID(long id) {
        Event event = null;
        DatabaseHelper databaseHelper =
                new DatabaseHelper(context);
        if(databaseHelper != null){
            SQLiteDatabase sqLiteDatabase =
                    databaseHelper.getWritableDatabase();
            Cursor cursor = sqLiteDatabase.query(
                    DatabaseScheme.TABLE_EVENTS,
                    null,
                    DatabaseScheme._ID + "=?",
                    new String[] {String.valueOf(id)},
                    null,
                    null,
                    null);
            try {
                if(cursor != null && cursor.getCount() > 0){
                    if(cursor.moveToNext()) {
                        event = cursorToEntity(cursor);
                    }
                }
            } finally {
                if(cursor != null){
                    cursor.close();
                }
                sqLiteDatabase.close();
            }
        }
        return event;
    }

    @Override
    public List<Event> getEventsByDate(Date calendar) {
        List<Event> eventList = new ArrayList<>();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        if (databaseHelper != null) {
            SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
            Cursor cursor = sqLiteDatabase.query(
                    DatabaseScheme.TABLE_EVENTS,
                    null,
                    DatabaseScheme.C_E_START + " LIKE "
                        + "\"%" + DateUtil.getStringDateFromDate(calendar) + "%\"",
                    null,
                    null,
                    null,
                    DatabaseScheme.C_E_START);
            try {
                if (cursor != null && cursor.getCount() > 0) {
                    while(cursor.moveToNext()){
                        eventList.add(cursorToEntity(cursor));
                    }
                }
            } finally {
                if(cursor != null){
                    cursor.close();
                }
                sqLiteDatabase.close();
            }
        }
        return eventList;
    }

    private ContentValues entityToContentValues(Event event){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseScheme.C_E_NAME,
                event.getTitle());
        contentValues.put(DatabaseScheme.C_E_DESCRIPTION,
                event.getDescription());
        contentValues.put(DatabaseScheme.C_E_CATEGORY,
                event.getCategory());
        contentValues.put(DatabaseScheme.C_E_START,
                DateUtil.getStringFromDate(event.getStartDate()));
        contentValues.put(DatabaseScheme.C_E_END,
                DateUtil.getStringFromDate(event.getEndDate()));
        if(event.isFinished()){
            contentValues.put(DatabaseScheme.C_E_FINISHED, 1);
        } else{
            contentValues.put(DatabaseScheme.C_E_FINISHED, 0);
        }
        if(event.isNotificationAllowed()){
            contentValues.put(DatabaseScheme.C_E_NOTIFICATIONS, 1);
            contentValues.put(DatabaseScheme.C_E_NOTIFICATIONS_BEFORE,
                    event.getNotificationBefore());
        } else {
            contentValues.put(DatabaseScheme.C_E_NOTIFICATIONS, 0);
        }
        contentValues.put(DatabaseScheme.C_E_TYPE, "event");

        return  contentValues;
    }

    private Event cursorToEntity(Cursor cursor){

        Event event = new Event();

        event.setId(cursor.getLong(
                cursor.getColumnIndex(DatabaseScheme._ID)));
        event.setDescription(cursor.getString(
                cursor.getColumnIndex(DatabaseScheme.C_E_DESCRIPTION)));

        event.setCategory(cursor.getString(
                cursor.getColumnIndex(DatabaseScheme.C_E_CATEGORY)));

        event.setTitle(cursor.getString(
                cursor.getColumnIndex(DatabaseScheme.C_E_NAME)));

        Date startDateTime = DateUtil
                .getDateFromString(
                        cursor.getString(
                                cursor.getColumnIndex(
                                        DatabaseScheme.C_E_START)));

        event.setStartDate(startDateTime);

        Date endDateTime = DateUtil
                .getDateFromString(
                        cursor.getString(
                                cursor.getColumnIndex(
                                        DatabaseScheme.C_E_END)));
        event.setEndDate(endDateTime);

        int finished = cursor.getInt(
                cursor.getColumnIndex(DatabaseScheme.C_E_FINISHED));
        if (finished == 1){
            event.setFinished(true);
        }

        int notificationAllowed = cursor.getInt(
                cursor.getColumnIndex(DatabaseScheme.C_E_NOTIFICATIONS));
        if(notificationAllowed==1){
            event.setNotificationAllowed(true);
            event.setNotificationBefore(cursor.getString(
                    cursor.getColumnIndex(
                            DatabaseScheme.C_E_NOTIFICATIONS_BEFORE)));
        } else {
            event.setNotificationBefore("Without notification");
        }
        event.setType(cursor.getString(
                cursor.getColumnIndex(
                        DatabaseScheme.C_E_TYPE)));
        return event;
    }

    private void setNotification(final Event event){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    Thread.sleep(DateUtil.getMilisTillEvent(event));
                    Load mLoad = PugNotification.with(context)
                            .load()
                            .title(event.getTitle())
                            .message(event.getDescription())
                            .smallIcon(R.drawable.ic_stat_timeo)
                            .largeIcon(R.drawable.ic_timeo);
                    mLoad.simple().build();
                    //todo PugNotification onClick event(start app and open details activity)
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
