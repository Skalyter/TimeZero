package com.example.timezero.database;

import com.example.timezero.model.Reminder;

import java.util.Date;
import java.util.List;

public interface IReminderDAO {
    void insertReminder(Reminder reminder);
    void updateReminder(Reminder reminder);
    void deleteReminder(Reminder reminder);
    List<Reminder> getAllReminders();
    List<Reminder> getRemindersByDate(Date calendar);
    Reminder getReminderByID(long id);
}
