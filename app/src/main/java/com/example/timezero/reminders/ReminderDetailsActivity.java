package com.example.timezero.reminders;

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
import com.example.timezero.database.ReminderDAO;
import com.example.timezero.model.Reminder;
import com.example.timezero.util.DateUtil;
import com.example.timezero.util.IntentConstants;

public class ReminderDetailsActivity extends AppCompatActivity {

    private long id;
    private Reminder reminder;
    private ReminderDAO reminderDAO;
    private static final int EDIT_REMINDER_CODE = 101;

    private TextView title, description, time, date, notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_details);
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
        reminderDAO = new ReminderDAO(this);
        findViewsById();
        setData();
        getSupportActionBar().setTitle(reminder.getTitle() + " " + getString(R.string.details));
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
                startActivityForResult(AddEditReminderActivity.getIntent(this,id),
                        EDIT_REMINDER_CODE);
                break;
            case R.id.action_delete:
                reminderDAO.deleteReminder(reminder);
                finish();
                break;
        }
        return true;
    }

    private void setData(){
        reminder = reminderDAO.getReminderByID(id);
        title.setText(reminder.getTitle());
        if(reminder.getDescription()!= null &&reminder.getDescription().length()>0) {
            description.setText(reminder.getDescription());
        } else{
            description.setText(R.string.no_description);
        }
        time.setText(DateUtil.getStringTimeFromDate(reminder.getStartDate()));
        date.setText(DateUtil.getStringDateFromDate(reminder.getStartDate()));
        notifications.setText(reminder.getNotificationBefore());
    }

    private void findViewsById(){
        title = findViewById(R.id.reminder_title);
        description = findViewById(R.id.reminder_description);
        time = findViewById(R.id.reminder_time);
        date = findViewById(R.id.reminder_date);
        notifications = findViewById(R.id.reminder_notification);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK
                && requestCode == EDIT_REMINDER_CODE){
            setData();
        }
    }

    public static Intent getIntent(Context context, Long id){
        Intent intent = new Intent(context, ReminderDetailsActivity.class);
        intent.putExtra(IntentConstants.ID, id);
        return intent;
    }
}
