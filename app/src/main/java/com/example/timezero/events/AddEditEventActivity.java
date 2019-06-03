package com.example.timezero.events;

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
import com.example.timezero.database.EventDAO;
import com.example.timezero.model.Event;
import com.example.timezero.util.DateUtil;
import com.example.timezero.util.IntentConstants;

import java.util.Calendar;
import java.util.Date;

import static com.example.timezero.util.SpinnerUtil.getIndex;
import static com.example.timezero.util.SpinnerUtil.getSelectedCategory;
import static com.example.timezero.util.SpinnerUtil.getSelectedNotification;

public class AddEditEventActivity extends AppCompatActivity {

    Spinner categorySpinner, notificationSpinner;
    Switch notificationSwitch;
    EditText title, description;
    TextView startDate, startTime, endDate, endTime;
    Date startDateTime, endDateTime;
    Calendar calendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener startDateSetListener, endDateSetListener;
    TimePickerDialog.OnTimeSetListener startTimeSetListener, endTimeSetListener;
    private long id;
    private Event event;
    private EventDAO eventDAO = new EventDAO(this);

    public static Intent getIntent(Context context, Long id) {
        Intent intent = new Intent(context, AddEditEventActivity.class);
        intent.putExtra(IntentConstants.ID, id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_event);
        Toolbar toolbar = findViewById(R.id.toolbar);

        title = findViewById(R.id.event_title);
        description = findViewById(R.id.event_description);
        startDate = findViewById(R.id.start_date);
        startTime = findViewById(R.id.start_time);
        endDate = findViewById(R.id.end_date);
        endTime = findViewById(R.id.end_time);
        notificationSpinner = findViewById(R.id.notification_spinner);
        notificationSwitch = findViewById(R.id.notification_switch);
        categorySpinner = findViewById(R.id.category_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.event_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.notification_options, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notificationSpinner.setAdapter(adapter1);

        setSupportActionBar(toolbar);
        //add the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        eventDAO = new EventDAO(this);

        //set id of event
        id = getIntent().getLongExtra(IntentConstants.ID, -1);
        if (id == -1 ) {
            getSupportActionBar().setTitle("Add new event");
            startDateTime = new Date();
            endDateTime = new Date();
        } else {
            //if you update an event, initialize thw fields with their values from db
            getSupportActionBar().setTitle("Update event");
            event = eventDAO.getEventByID(id);
            startDateTime = event.getStartDate();
            endDateTime = event.getEndDate();

            title.setText(event.getTitle());
            description.setText(event.getDescription());
            categorySpinner.setSelection(
                    getIndex(categorySpinner,
                            event.getCategory()));
            notificationSwitch.setChecked(
                    event.isNotificationAllowed());
            if (notificationSwitch.isChecked()) {
                notificationSpinner.setVisibility(View.VISIBLE);
                notificationSpinner.setSelection(getIndex(notificationSpinner,
                        event.getNotificationBefore()));
            } else {
                notificationSpinner.setVisibility(View.INVISIBLE);
            }
        }

        //CALENDAR manipulation
        startDate.setText(DateUtil.getStringDateFromDate(startDateTime));
        startTime.setText(DateUtil.getStringTimeFromDate(startDateTime));
        endDate.setText(DateUtil.getStringDateFromDate(endDateTime));
        endTime.setText(DateUtil.getStringTimeFromDate(endDateTime));

        //set value for notification switch
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (notificationSpinner.getVisibility() == View.VISIBLE) {
                    notificationSpinner.setVisibility(View.INVISIBLE);
                } else {
                    notificationSpinner.setVisibility(View.VISIBLE);
                }
            }
        });

        //CALENDAR SETTINGS
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddEditEventActivity.this,
                        android.R.style.Theme_DeviceDefault_Dialog, startDateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
        startDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                startDateTime = calendar.getTime();
                startDate.setText(DateUtil.getStringDateFromDate(startDateTime));
                if (!isChronologic()) {
                    setChronologic();
                }

            }
        };
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddEditEventActivity.this,
                        android.R.style.Theme_DeviceDefault_Dialog, endDateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
        endDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                endDateTime = calendar.getTime();
                if (!isChronologic()) {
                    setChronologic();

                } else {
                    endDate.setText(DateUtil.getStringDateFromDate(endDateTime));
                }
            }
        };

        //TIME SETTINGS
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        AddEditEventActivity.this,
                        android.R.style.Theme_DeviceDefault_Dialog,
                        startTimeSetListener,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();
            }
        });
        startTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        hourOfDay,
                        minute);
                startDateTime = calendar.getTime();
                startTime.setText(DateUtil.getStringTimeFromDate(startDateTime));
                if (!isChronologic()) {
                    setChronologic();
                }
            }
        };

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        AddEditEventActivity.this,
                        android.R.style.Theme_DeviceDefault_Dialog,
                        endTimeSetListener,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();
            }
        });
        endTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        hourOfDay,
                        minute);
                endDateTime = calendar.getTime();
                if (!isChronologic()) {
                    setChronologic();
                } else {
                    endTime.setText(DateUtil.getStringTimeFromDate(endDateTime));
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_edit_event, menu);
        return true;
    }

    public boolean isChronologic(){
        return startDateTime.before(endDateTime);
    }

    public void setChronologic(){
        endDateTime = startDateTime;
        endDate.setText(startDate.getText());
        endTime.setText(startTime.getText());
        calendar.setTime(startDateTime);
        Toast.makeText(AddEditEventActivity.this, "Event should not end before it is supposed to start", Toast.LENGTH_SHORT).show();
    }

    public void save(MenuItem item) {
        save();
    }

    public void save() {
        String titleInput = title.getText().toString();
        String descriptionInput = description.getText().toString();
        String startDateTimeInput = startDate.getText().toString() + " " + startTime.getText().toString();
        String endDateTimeInput = endDate.getText().toString() + " " + endTime.getText().toString();
        String category = getSelectedCategory(categorySpinner);
        boolean notifyBefore = notificationSwitch.isChecked();
        String notificationBefore = getSelectedNotification(notificationSpinner);

        if (titleInput.equals("")) {
            title.setError("Fill in the name");
        } else if (startDateTime.after(endDateTime)) {
            Toast.makeText(this, "Start date should not be before end date", Toast.LENGTH_SHORT).show();
        } else {
            if (id == -1) {
                event = new Event();
                event.setTitle(titleInput);
                event.setDescription(descriptionInput);
                event.setCategory(category);
                event.setStartDate(DateUtil.getDateFromString(startDateTimeInput));
                event.setEndDate(DateUtil.getDateFromString(endDateTimeInput));
                event.setNotificationAllowed(notifyBefore);
                if (notifyBefore) {
                    event.setNotificationBefore(notificationBefore);
                } else {
                    event.setNotificationBefore(getString(R.string.without_notification));
                }
                eventDAO.insertEvent(event);
            } else {
                event.setTitle(titleInput);
                event.setDescription(descriptionInput);
                event.setCategory(category);

                event.setStartDate(DateUtil.getDateFromString(startDateTimeInput));
                event.setEndDate(DateUtil.getDateFromString(endDateTimeInput));
                event.setNotificationAllowed(notifyBefore);
                if (notifyBefore) {
                    event.setNotificationBefore(notificationBefore);
                } else {
                    event.setNotificationBefore(getString(R.string.without_notification));
                }
                eventDAO.updateEvent(event);
            }
            setResult(RESULT_OK);
            finish();
            Toast.makeText(this, "Event saved", Toast.LENGTH_SHORT).show();
        }
    }
}
