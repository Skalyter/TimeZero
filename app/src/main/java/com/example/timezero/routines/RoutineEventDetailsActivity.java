package com.example.timezero.routines;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.timezero.R;
import com.example.timezero.database.DayOfWeekDAO;
import com.example.timezero.database.RoutineEventDAO;
import com.example.timezero.model.DayOfWeek;
import com.example.timezero.model.RoutineEvent;
import com.example.timezero.util.DateUtil;
import com.example.timezero.util.IntentConstants;

import java.util.List;

public class RoutineEventDetailsActivity extends AppCompatActivity {

    private static final int EDIT_ROUTINE_EVENT_CODE = 103;
    private long id, routineId;
    private TextView title, description, start, end, repeat, notification;
    private RoutineEvent routineEvent;
    private RoutineEventDAO routineEventDAO;

    public static Intent getIntent(Context context, long id) {
        Intent intent = new Intent(context, RoutineEventDetailsActivity.class);
        intent.putExtra(IntentConstants.ID, id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_event_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        id = getIntent().getLongExtra(IntentConstants.ID, -1);
        routineEventDAO = new RoutineEventDAO(this);
        //routineId = routineEventDAO.getRoutineId(id);
        routineEvent = routineEventDAO.getRoutineEventByID(id);
        routineId = routineEvent.getRoutineId();
        findViewsById();
        setData();
        getSupportActionBar().setTitle(routineEvent.getTitle() + " " + getString(R.string.details));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                routineEventDAO.deleteRoutineEvent(routineEvent);
                finish();
                break;
            case R.id.action_edit:
                startActivityForResult(AddEditRoutineEventActivity.getIntent(this, id, routineId),
                        EDIT_ROUTINE_EVENT_CODE);
                break;
        }
        return true;
    }

    private void findViewsById() {
        title = findViewById(R.id.r_ev_title);
        description = findViewById(R.id.r_ev_description);
        start = findViewById(R.id.r_ev_start_time);
        end = findViewById(R.id.r_ev_end_time);
        repeat = findViewById(R.id.r_ev_repeat);
        notification = findViewById(R.id.r_ev_notification);
    }

    private void setData() {
        routineEvent = routineEventDAO.getRoutineEventByID(id);
        title.setText(routineEvent.getTitle());
        if (routineEvent.getDescription() != null && routineEvent.getDescription().length()>0) {
            description.setText(routineEvent.getDescription());
        } else {
            description.setText(getString(R.string.no_description));
        }
        start.setText(DateUtil.getStringTimeFromDate(routineEvent.getStartDate()));
        end.setText(DateUtil.getStringTimeFromDate(routineEvent.getEndTime()));
        List<DayOfWeek> days = new DayOfWeekDAO(this).getDaysOfWeekByRoutineEventId(id);
        if (days.size() == 7) {
            repeat.setText(getString(R.string.daily));
        } else if (days.size() == 5
                && days.get(0).getNumberOfDay() == 1
                && days.get(1).getNumberOfDay() == 2
                && days.get(2).getNumberOfDay() == 3
                && days.get(3).getNumberOfDay() == 4
                && days.get(4).getNumberOfDay() == 5) {
            repeat.setText(getString(R.string.working_days));
        } else if (days.size() == 2
                && days.get(0).getNumberOfDay() == 6
                && days.get(1).getNumberOfDay() == 7) {
            repeat.setText(getString(R.string.weekend));
        } else {
            String repetition = "";
            for (DayOfWeek day : days) {
                repetition += DateUtil.getDayOfWeek(day.getNumberOfDay()) + " ";

            }
            repeat.setText(repetition);
        }
        notification.setText(routineEvent.getNotificationBefore());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_ROUTINE_EVENT_CODE
                && resultCode == RESULT_OK) {
            setData();
        }
    }
}
