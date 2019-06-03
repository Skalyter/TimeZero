package com.example.timezero.database;

import com.example.timezero.model.Routine;

import java.util.List;

public interface IRoutineDAO {
    void insertRoutine(Routine routine);
    void updateRoutine(Routine routine);
    void deleteRoutine(Routine routine);
    List<Routine> getAllRoutines();
    Routine getRoutineByID(long id);
}
