package com.example.timezero.model;

public class DayOfWeek {
    private Long id;
    private int numberOfDay;
    private int routineEventId;

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getNumberOfDay() {
        return numberOfDay;
    }

    public void setNumberOfDay(int numberOfDay) {
        this.numberOfDay = numberOfDay;
    }

    public int getRoutineEventId() {
        return routineEventId;
    }

    public void setRoutineEventId(int routineEventId) {
        this.routineEventId = routineEventId;
    }
}
