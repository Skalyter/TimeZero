package com.example.timezero.model;

public class RoutineEventItem extends ListItem {

    private RoutineEvent routineEvent;

    public RoutineEvent getRoutineEvent() {
        return routineEvent;
    }

    public void setRoutineEvent(RoutineEvent routineEvent) {
        this.routineEvent = routineEvent;
    }

    @Override
    public int getType() {
        return TYPE_EVENT;
    }
}
