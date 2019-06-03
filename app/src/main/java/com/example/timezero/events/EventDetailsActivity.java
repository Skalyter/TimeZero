package com.example.timezero.events;

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
import com.example.timezero.database.EventDAO;
import com.example.timezero.model.Event;
import com.example.timezero.util.DateUtil;
import com.example.timezero.util.IntentConstants;

public class EventDetailsActivity extends AppCompatActivity {

    private Event event;
    private EventDAO eventDAO;
    private long id;

    private static final int EDIT_EVENT_CODE = 102;

    private TextView title, description, category, startDate,
            startTime, endDate, endTime, notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
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
        eventDAO = new EventDAO(this);
        findViewsById();
        setData();
        getSupportActionBar().setTitle(event.getTitle() +" " + getString(R.string.details));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_edit:
                startActivityForResult(AddEditEventActivity.getIntent(this,id),
                        EDIT_EVENT_CODE);
                break;
            case R.id.action_delete:
                eventDAO.deleteEvent(event);
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == EDIT_EVENT_CODE
        && resultCode == RESULT_OK){
            setData();
        }
    }

    private void findViewsById(){
        title = findViewById(R.id.event_title);
        description = findViewById(R.id.event_description);
        category = findViewById(R.id.event_category);
        startDate = findViewById(R.id.event_start_date);
        startTime = findViewById(R.id.event_start_time);
        endDate = findViewById(R.id.event_end_date);
        endTime = findViewById(R.id.event_end_time);
        notifications = findViewById(R.id.event_notification);
    }

    private void setData(){
        event = eventDAO.getEventByID(id);
        title.setText(event.getTitle());
        if(event.getDescription() != null && event.getDescription().length()>0) {
            description.setText(event.getDescription());
        } else {
            description.setText(getString(R.string.no_description));
        }
        category.setText(event.getCategory());
        startDate.setText(DateUtil.getStringDateFromDate(event.getStartDate()));
        startTime.setText(DateUtil.getStringTimeFromDate(event.getStartDate()));
        endDate.setText(DateUtil.getStringDateFromDate(event.getEndDate()));
        endTime.setText(DateUtil.getStringTimeFromDate(event.getEndDate()));
        notifications.setText(event.getNotificationBefore());
    }

    public static Intent getIntent(Context context, long id){
        Intent intent = new Intent(context, EventDetailsActivity.class);
        intent.putExtra(IntentConstants.ID, id);
        return intent;
    }

}
