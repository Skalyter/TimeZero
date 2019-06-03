package com.example.timezero.util;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;

import com.example.timezero.R;
import com.example.timezero.model.Activity;

import br.com.goncalves.pugnotification.notification.Load;
import br.com.goncalves.pugnotification.notification.PugNotification;

public class NotificationJobService extends JobService {

    Context context;
    Activity event;

    public NotificationJobService() {
    }

    public NotificationJobService(Context context, Activity event) {
        this.context = context;
        this.event = event;
    }

    boolean jobCancelled = false;
    @Override
    public boolean onStartJob(JobParameters params) {
        setNotification(params);
        return true;
    }

    private void setNotification(final JobParameters parameters){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(jobCancelled){
                        return;
                    }
                    Thread.sleep(DateUtil.getMilisTillEvent(event));
                    Load mLoad = PugNotification.with(context)
                            .load()
                            .title(event.getTitle())
                            .message(event.getDescription())
                            .smallIcon(R.drawable.ic_stat_timeo)
                            .largeIcon(R.drawable.ic_timeo);
                    mLoad.simple().build();
                    //todo PugNotification onClick event(start app and open details activity)
                    jobFinished(parameters, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    @Override
    public boolean onStopJob(JobParameters params) {
        jobCancelled = true;
        return true;
    }
}
