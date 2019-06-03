package com.example.timezero.model;

public class Reminder extends Activity{

    private String description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
