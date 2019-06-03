package com.example.timezero.database;

import com.example.timezero.model.Event;

import java.util.Date;
import java.util.List;

public interface IEventDAO {
    void insertEvent( Event event);
    void updateEvent(Event event);
    void deleteEvent(Event event);
    List<Event> getAllEvents();
    Event getEventByID(long id);
    List<Event> getEventsByDate(Date calendar);
}
