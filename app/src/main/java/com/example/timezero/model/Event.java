package com.example.timezero.model;

import java.util.Date;

public class Event extends Activity {

    private String description;
    private String category;
    private Date endDate;
    private boolean finished;
    private boolean notificationAllowed;
    private String notificationBefore;

    public String getNotificationBefore() {
        return notificationBefore;
    }

    public void setNotificationBefore(String notificationBefore) {
        this.notificationBefore = notificationBefore;
    }

    public boolean isNotificationAllowed() {
        return notificationAllowed;
    }

    public void setNotificationAllowed(boolean notificationAllowed) {
        this.notificationAllowed = notificationAllowed;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
