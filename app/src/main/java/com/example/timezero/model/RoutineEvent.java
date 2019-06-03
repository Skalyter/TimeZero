package com.example.timezero.model;

import java.util.Date;

public class RoutineEvent extends Activity {

    private Long routineId;
    private Date endTime;
    private boolean notificationAllowed;
    private String notificationBefore;

    public String getNotificationBefore() {
        return notificationBefore;
    }

    public void setNotificationBefore(String notificationBefore) {
        this.notificationBefore = notificationBefore;
    }

    public Long getRoutineId() {
        return routineId;
    }

    public void setRoutineId(long routineId) {
        this.routineId = routineId;
    }

    public boolean isNotificationAllowed() {
        return notificationAllowed;
    }

    public void setNotificationAllowed(boolean notificationAllowed) {
        this.notificationAllowed = notificationAllowed;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
