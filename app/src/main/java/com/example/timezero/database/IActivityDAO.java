package com.example.timezero.database;

import com.example.timezero.model.Activity;

import java.util.Date;
import java.util.List;

public interface IActivityDAO {
    List<Activity> getActivitiesByDate(Date date);
}
