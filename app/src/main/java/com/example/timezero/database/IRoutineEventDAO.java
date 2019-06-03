package com.example.timezero.database;

import com.example.timezero.model.RoutineEvent;

import java.util.List;

public interface IRoutineEventDAO {
    void insertRoutineEvent(RoutineEvent routineEvent);
    void updateRoutineEvent(RoutineEvent routineEvent);
    void deleteRoutineEvent(RoutineEvent routineEvent);
    void deleteRoutineEventsForRoutine(long id);
    List<RoutineEvent> getRoutineEventsByDate(int dayOfWeek);
    List<RoutineEvent> getRoutineEventsForRoutine(long routineId, int dayOfWeek);
    RoutineEvent getRoutineEventByID(long id);
    long getRoutineId(long routineEventId);
}
