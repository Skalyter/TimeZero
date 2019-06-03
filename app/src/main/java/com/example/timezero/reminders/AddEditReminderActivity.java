package com.example.timezero.reminders;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.timezero.R;
import com.example.timezero.database.ReminderDAO;
import com.example.timezero.model.Reminder;
import com.example.timezero.util.DateUtil;
import com.example.timezero.util.IntentConstants;

import java.util.Calendar;
import java.util.Date;

import static com.example.timezero.util.SpinnerUtil.getIndex;
import static com.example.timezero.util.SpinnerUtil.getSelectedNotification;

public class AddEditReminderActivity extends AppCompatActivity {

    private Reminder reminder;
    private ReminderDAO reminderDAO;
    private long id;

    private Switch notificationSwitch;
    private Spinner notificationSpinner;
    private Calendar calendar = Calendar.getInstance();
    private Date date;
    private TextView calendarText, timeText;
    private EditText titleEditText, descriptionEditText;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;


    public static Intent getIntent(Context context, Long id) {
        Intent intent = new Intent(context, AddEditReminderActivity.class);
        intent.putExtra(IntentConstants.ID, id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_reminder);

        //find views by id
        Toolbar toolbar = findViewById(R.id.toolbar);
        notificationSwitch = findViewById(R.id.reminder_notification_switch);
        timeText = findViewById(R.id.reminder_time);
        notificationSpinner = findViewById(R.id.reminder_spinner);
        calendarText = findViewById(R.id.reminder_date);
        titleEditText = findViewById(R.id.reminder_title);
        descriptionEditText = findViewById(R.id.reminder_description);

        setSupportActionBar(toolbar);
        //set back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        //initialize a reminderDAO
        reminderDAO = new ReminderDAO(this);

        //get reminder id (if !=-1 you have to edit an existing one)
        id = getIntent().getLongExtra(IntentConstants.ID, -1);
        if (id == -1) {
            date = new Date();
            getSupportActionBar().setTitle("Add new reminder");
            calendarText.setText(DateUtil.getStringDateFromDate(date));
            timeText.setText(DateUtil.getStringTimeFromDate(date));
        } else {
            //initialize the fields with data from db
            getSupportActionBar().setTitle("Change reminder");
            reminder = reminderDAO.getReminderByID(id);
            date = reminder.getStartDate();

            titleEditText.setText(reminder.getTitle());
            descriptionEditText.setText(reminder.getDescription());
            timeText.setText(DateUtil.getStringTimeFromDate(date));
            calendarText.setText(DateUtil.getStringDateFromDate(date));
            notificationSwitch.setChecked(reminder.isNotificationAllowed());
            if (notificationSwitch.isChecked()) {
                notificationSpinner.setVisibility(View.VISIBLE);
                notificationSpinner.setSelection(getIndex(notificationSpinner,
                        reminder.getNotificationBefore()));
            } else {
                notificationSpinner.setVisibility(View.INVISIBLE);
            }
        }
        //initialize the notification switch and spinner obj
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (notificationSpinner.getVisibility() == View.INVISIBLE) {
                    notificationSpinner.setVisibility(View.VISIBLE);
                } else {
                    notificationSpinner.setVisibility(View.INVISIBLE);
                }
            }
        });

        // Create an ArrayAdapter using the string array and a default notificationSpinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.notification_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notificationSpinner.setAdapter(adapter);

        //onClickListeners for date and time
        calendarText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddEditReminderActivity.this,
                        android.R.style.Theme_DeviceDefault_Dialog, dateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                date = calendar.getTime();
                calendarText.setText(DateUtil.getStringDateFromDate(date));
            }
        };
        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        AddEditReminderActivity.this,
                        android.R.style.Theme_DeviceDefault_Dialog,
                        timeSetListener,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();
            }
        });
        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //calendar = Calendar.getInstance();
                calendar.set(calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        hourOfDay,
                        minute);
                date = calendar.getTime();
                timeText.setText(DateUtil.getStringTimeFromDate(date));
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_edit_reminder, menu);
        return true;
    }

    public void save(MenuItem item) {
        save();
    }

    private void save() {
        String titleInput = titleEditText.getText().toString();
        String descriptionInput = descriptionEditText.getText().toString();
        String dateTimeInput = calendarText.getText().toString() + " " + timeText.getText().toString();
        boolean notifyBefore = notificationSwitch.isChecked();
        String notificationBefore = getSelectedNotification(notificationSpinner);
        if (titleInput.equals("")) {
            titleEditText.setError("Fill in the name");
        } else {
            if (id == -1) {
                reminder = new Reminder();
                reminder.setTitle(titleInput);
                reminder.setDescription(descriptionInput);
                reminder.setStartDate(DateUtil.getDateFromString(dateTimeInput));
                reminder.setNotificationAllowed(notifyBefore);
                if (notifyBefore) {
                    reminder.setNotificationBefore(notificationBefore);
                } else {
                    reminder.setNotificationBefore("Without notification");
                }
                reminderDAO.insertReminder(reminder);
            } else {
                reminder.setTitle(titleInput);
                reminder.setDescription(descriptionInput);
                reminder.setStartDate(DateUtil.getDateFromString(dateTimeInput));
                reminder.setNotificationAllowed(notifyBefore);
                if (notifyBefore) {
                    reminder.setNotificationBefore(notificationBefore);
                } else {
                    reminder.setNotificationBefore("Without notification");
                }
                reminderDAO.updateReminder(reminder);
            }
            setResult(RESULT_OK);
            finish();
            Toast.makeText(this, "Reminder saved", Toast.LENGTH_SHORT).show();
        }
    }
}