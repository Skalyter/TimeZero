package com.example.timezero.model;

import com.example.timezero.util.DateUtil;

import java.util.Date;

public class Activity implements Comparable<Activity>{

    private Date startDate;
    private Long id;
    private String title;
    private String description;
    private String type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public int compareTo(Activity activity) {
        if(getStartDate() == null || activity.getStartDate() == null) {
            return 0;
        }
        String start1 = DateUtil.getStringTimeFromDate(startDate);
        String start2 = DateUtil.getStringTimeFromDate(activity.startDate);
        return start1.compareTo(start2);
    }
}
