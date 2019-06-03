package com.example.timezero.database;

import android.content.Context;

import com.example.timezero.model.Activity;
import com.example.timezero.model.Event;
import com.example.timezero.model.Reminder;
import com.example.timezero.model.RoutineEvent;
import com.example.timezero.util.DateUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ActivityDAO implements IActivityDAO {

    private Context context;

    public ActivityDAO(Context context) {
        this.context = context;
    }

    @Override
    public List<Activity> getActivitiesByDate(Date date) {

        int dayOfWeek = DateUtil.getDayOfWeek(date);
        EventDAO eventDAO = new EventDAO(context);
        ReminderDAO reminderDAO = new ReminderDAO(context);
        RoutineEventDAO routineEventDAO = new RoutineEventDAO(context);

        List<Event> events = eventDAO.getEventsByDate(date);
        List<Reminder> reminders = reminderDAO.getRemindersByDate(date);
        List<RoutineEvent> routineEvents = routineEventDAO.getRoutineEventsByDate(dayOfWeek);

        List<Activity> activities = new ArrayList<>();
        activities.addAll(routineEvents);
        activities.addAll(events);
        activities.addAll(reminders);
        Collections.sort(activities);
        return activities;
    }
}
