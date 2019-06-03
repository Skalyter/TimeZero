package com.example.timezero.util;

import android.widget.Spinner;

public class SpinnerUtil {

    public static int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        return 0;
    }

    public static String getSelectedCategory (Spinner spinner){
        String category;
        switch (spinner.getSelectedItemPosition()){
            case 0:
                category = "School";
                break;
            case 1:
                category = "Work";
                break;
            case 2:
                category = "Personal";
                break;
            case 3:
                category = "Birthday";
                break;
            case 4:
                category = "Holiday";
                break;
            case 5:
                category = "Day Off";
                break;
            default:
                category = "Others";
        }
        return category;
    }

    public static String getSelectedNotification (Spinner spinner){
        String notificationBefore;
        switch (spinner.getSelectedItemPosition()){
            case 0:
                notificationBefore = "5 minutes before";
                break;
            case 1:
                notificationBefore = "10 minutes before";
                break;
            case 2:
                notificationBefore = "15 minutes before";
                break;
            case 3:
                notificationBefore = "30 minutes before";
                break;
            case 4:
                notificationBefore = "1 hour before";
                break;
            case 5:
                notificationBefore = "2 hours before";
                break;
            default:
                notificationBefore = "Without notification";
        }
        return notificationBefore;
    }
}
