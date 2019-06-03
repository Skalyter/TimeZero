package com.example.timezero.database;

import com.example.timezero.model.DayOfWeek;

import java.util.List;

public interface IDayOfWeekDAO {
    void insertDayOfWeek(DayOfWeek dayOfWeek);
    void updateDayOfWeek(DayOfWeek dayOfWeek, int routineEventId);
    void deleteDayOfWeek(DayOfWeek dayOfWeek);
    DayOfWeek getDayOfWeekById(long id);
    List<DayOfWeek> getDaysOfWeekByRoutineEventId(long id);
}
