package com.example.timezero.routines;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.timezero.R;
import com.example.timezero.database.DayOfWeekDAO;
import com.example.timezero.database.RoutineEventDAO;
import com.example.timezero.model.DayOfWeek;
import com.example.timezero.model.RoutineEvent;
import com.example.timezero.util.DateUtil;
import com.example.timezero.util.IntentConstants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.timezero.util.SpinnerUtil.getIndex;
import static com.example.timezero.util.SpinnerUtil.getSelectedNotification;

public class AddEditRoutineEventActivity extends AppCompatActivity {

    private RoutineEvent routineEvent;
    private RoutineEventDAO routineEventDAO;
    private long id;
    private long routineId;
    private Date startDate, endDate;

    private Calendar calendar = Calendar.getInstance();
    private TimePickerDialog.OnTimeSetListener timeSetListener;
    private TimePickerDialog.OnTimeSetListener endTimeSetListener;

    private List<DayOfWeek> dayOfWeekList = new ArrayList<>();
    private DayOfWeekDAO dayOfWeekDAO;

    private EditText eventTitle, eventDescription;
    private TextView start, end;
    private RadioButton daily, weekDays, weekend, custom;
    private Switch notificationSwitch;
    private CheckBox mon, tue, wed, thu, fri, sat, sun;
    private Spinner notificationSpinner;

    public static Intent getIntent(Context context, Long id, Long routineId) {
        Intent intent = new Intent(context, AddEditRoutineEventActivity.class);
        intent.putExtra(IntentConstants.ID, id);
        intent.putExtra(IntentConstants.ROUTINE_ID, routineId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_routine_event);
        Toolbar toolbar = findViewById(R.id.toolbar);
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
        findViewsById();

        routineEventDAO = new RoutineEventDAO(this);
        dayOfWeekDAO = new DayOfWeekDAO(this);

        //get routineEvent id from the intent (if there isn't any, default value is -1)
        id = getIntent().getLongExtra(IntentConstants.ID, -1);
        routineId = getIntent().getLongExtra(IntentConstants.ROUTINE_ID, 1);

        //NOTIFICATION SPINNER HANDLER
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.notification_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notificationSpinner.setAdapter(adapter);

        if (id == -1) {
            //create a new routineEvent
            getSupportActionBar().setTitle("Add new routine event");
            startDate = new Date();
            endDate = new Date();
            start.setText(DateUtil.getStringTimeFromDate(startDate));
            end.setText(DateUtil.getStringTimeFromDate(startDate));
        } else {
            //update an existing routineEvent and initialize the fields with its values
            routineEvent = routineEventDAO.getRoutineEventByID(id);
            dayOfWeekList = dayOfWeekDAO.getDaysOfWeekByRoutineEventId(id);

            getSupportActionBar().setTitle(
                    routineEvent.getTitle());

            eventTitle.setText(routineEvent.getTitle());
            eventDescription.setText(routineEvent.getDescription());

            startDate = routineEvent.getStartDate();
            endDate = routineEvent.getEndTime();

            start.setText(DateUtil.getStringTimeFromDate(routineEvent.getStartDate()));
            end.setText(DateUtil.getStringTimeFromDate(routineEvent.getEndTime()));

            if (dayOfWeekList.size() == 7) {
                daily.setChecked(true);
            } else if (dayOfWeekList.size() == 5
                    && dayOfWeekList.get(0).getNumberOfDay() == 1
                    && dayOfWeekList.get(1).getNumberOfDay() == 2
                    && dayOfWeekList.get(2).getNumberOfDay() == 3
                    && dayOfWeekList.get(3).getNumberOfDay() == 4
                    && dayOfWeekList.get(4).getNumberOfDay() == 5) {
                weekDays.setChecked(true);

            } else if (dayOfWeekList.size() == 2
                    && dayOfWeekList.get(0).getNumberOfDay() == 6
                    && dayOfWeekList.get(1).getNumberOfDay() == 7) {
                weekend.setChecked(true);

            } else {
                custom.setChecked(true);
                findViewById(R.id.routine_event_days).setVisibility(View.VISIBLE);
                for (int i = 0; i < dayOfWeekList.size(); i++) {
                    if (dayOfWeekList.get(i).getNumberOfDay() == 1) {
                        mon.setChecked(true);
                        continue;
                    }
                    if (dayOfWeekList.get(i).getNumberOfDay() == 2) {
                        tue.setChecked(true);
                        continue;
                    }
                    if (dayOfWeekList.get(i).getNumberOfDay() == 3) {
                        wed.setChecked(true);
                        continue;
                    }
                    if (dayOfWeekList.get(i).getNumberOfDay() == 4) {
                        thu.setChecked(true);
                        continue;
                    }
                    if (dayOfWeekList.get(i).getNumberOfDay() == 5) {
                        fri.setChecked(true);
                        continue;
                    }
                    if (dayOfWeekList.get(i).getNumberOfDay() == 6) {
                        sat.setChecked(true);
                        continue;
                    }
                    if (dayOfWeekList.get(i).getNumberOfDay() == 7) {
                        sun.setChecked(true);
                    }
                }
            }

            notificationSwitch.setChecked(
                    routineEvent.isNotificationAllowed());
            if (notificationSwitch.isChecked()) {
                notificationSpinner.setVisibility(View.VISIBLE);
                notificationSpinner.setSelection(getIndex(notificationSpinner,
                        routineEvent.getNotificationBefore()));
            } else {
                notificationSpinner.setVisibility(View.GONE);
            }
        }

        //TIME PICKER MANIPULATION
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        AddEditRoutineEventActivity.this,
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
                calendar = Calendar.getInstance();
                calendar.set(calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        hourOfDay,
                        minute);
                startDate = calendar.getTime();
                if (endDate.after(startDate)) {
                    start.setText(DateUtil.getStringTimeFromDate(startDate));
                } else {
                    start.setText(DateUtil.getStringTimeFromDate(startDate));
                    end.setText(DateUtil.getStringTimeFromDate(startDate));
                }
            }
        };

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        AddEditRoutineEventActivity.this,
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
                calendar = Calendar.getInstance();
                calendar.set(calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        hourOfDay,
                        minute);
                endDate = calendar.getTime();
                if (endDate.after(startDate)) {
                    end.setText(DateUtil.getStringTimeFromDate(endDate));
                } else {
                    endDate = startDate;
                    calendar.setTime(endDate);
                    end.setText(DateUtil.getStringTimeFromDate(startDate));
                    start.setText(DateUtil.getStringTimeFromDate(startDate));
                    Toast.makeText(AddEditRoutineEventActivity.this, "Event should not end before it is supposed to start", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_edit_event, menu);
        return true;
    }

    public void onRadioButtonClicked(View view) {

        //set routine event days checkboxes visible/gone, according to the selected radio button
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.daily:
                if (checked) {
                    if (findViewById(R.id.routine_event_days).getVisibility() == View.VISIBLE) {
                        findViewById(R.id.routine_event_days).setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.working_days:
                if (checked) {
                    if (findViewById(R.id.routine_event_days).getVisibility() == View.VISIBLE) {
                        findViewById(R.id.routine_event_days).setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.weekend:
                if (checked) {
                    if (findViewById(R.id.routine_event_days).getVisibility() == View.VISIBLE) {
                        findViewById(R.id.routine_event_days).setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.custom:
                if (checked) {
                    findViewById(R.id.routine_event_days).setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    public void findViewsById() {
        eventTitle = findViewById(R.id.routine_event_title);
        eventDescription = findViewById(R.id.routine_event_description);

        start = findViewById(R.id.routine_event_start);
        end = findViewById(R.id.routine_event_end);

        daily = findViewById(R.id.daily);
        weekDays = findViewById(R.id.working_days);
        weekend = findViewById(R.id.weekend);
        custom = findViewById(R.id.custom);

        mon = findViewById(R.id.monday_check);
        tue = findViewById(R.id.tuesday_check);
        wed = findViewById(R.id.wednesday_check);
        thu = findViewById(R.id.thursday_check);
        fri = findViewById(R.id.friday_check);
        sat = findViewById(R.id.saturday_check);
        sun = findViewById(R.id.sunday_check);

        notificationSpinner = findViewById(R.id.routine_event_spinner);
        notificationSwitch = findViewById(R.id.notification_switch);
    }

    public void save(MenuItem item) {

        save();
        setResult(RESULT_OK);
    }

    private void save() {
        String titleUserInput = eventTitle.getText().toString();
        String descriptionUserInput = eventDescription.getText().toString();
        String notificationBefore = getSelectedNotification(notificationSpinner);

        if (titleUserInput.equals("")) {
            eventTitle.setError("Fill in the title");
        } else if (!daily.isChecked()
                && !weekDays.isChecked()
                && !weekend.isChecked()
                && !custom.isChecked()) {
            daily.setError("Choose the repetition of the activity");
        } else if (custom.isChecked() && !mon.isChecked()
                && !tue.isChecked() && !wed.isChecked()
                && !thu.isChecked() && !fri.isChecked()
                && !sat.isChecked() && !sun.isChecked()) {
            custom.setError("Select at least one day of the week");
        } else {
            if (id == -1) {
                routineEvent = new RoutineEvent();
            }
            dayOfWeekList = new ArrayList<>();
            routineEvent.setTitle(titleUserInput);
            routineEvent.setDescription(descriptionUserInput);
            routineEvent.setStartDate(startDate);
            routineEvent.setEndTime(endDate);
            routineEvent.setRoutineId(routineId);
            if (notificationSwitch.isChecked()) {
                routineEvent.setNotificationAllowed(true);
                routineEvent.setNotificationBefore(notificationBefore);
            } else {
                routineEvent.setNotificationBefore("Without notification");
            }
            if (daily.isChecked()) {
                for (int i = 1; i <= 7; i++) {
                    DayOfWeek day = new DayOfWeek();
                    day.setNumberOfDay(i);
                    dayOfWeekList.add(day);
                }
            } else if (weekDays.isChecked()) {
                for (int i = 1; i <= 5; i++) {
                    DayOfWeek day = new DayOfWeek();
                    day.setNumberOfDay(i);
                    dayOfWeekList.add(day);
                }
            } else if (weekend.isChecked()) {
                for (int i = 6; i <= 7; i++) {
                    DayOfWeek day = new DayOfWeek();
                    day.setNumberOfDay(i);
                    dayOfWeekList.add(day);
                }
            } else {
                if (mon.isChecked()) {
                    DayOfWeek dayOfWeek = new DayOfWeek();
                    dayOfWeek.setNumberOfDay(1);
                    dayOfWeekList.add(dayOfWeek);
                }
                if (tue.isChecked()) {

                    DayOfWeek dayOfWeek = new DayOfWeek();
                    dayOfWeek.setNumberOfDay(2);
                    dayOfWeekList.add(dayOfWeek);
                }
                if (wed.isChecked()) {

                    DayOfWeek dayOfWeek = new DayOfWeek();
                    dayOfWeek.setNumberOfDay(3);
                    dayOfWeekList.add(dayOfWeek);
                }
                if (thu.isChecked()) {

                    DayOfWeek dayOfWeek = new DayOfWeek();
                    dayOfWeek.setNumberOfDay(4);
                    dayOfWeekList.add(dayOfWeek);
                }
                if (fri.isChecked()) {

                    DayOfWeek dayOfWeek = new DayOfWeek();
                    dayOfWeek.setNumberOfDay(5);
                    dayOfWeekList.add(dayOfWeek);
                }
                if (sat.isChecked()) {

                    DayOfWeek dayOfWeek = new DayOfWeek();
                    dayOfWeek.setNumberOfDay(6);
                    dayOfWeekList.add(dayOfWeek);
                }
                if (sun.isChecked()) {

                    DayOfWeek dayOfWeek = new DayOfWeek();
                    dayOfWeek.setNumberOfDay(7);
                    dayOfWeekList.add(dayOfWeek);
                }
            }
            if (id == -1) {
                routineEventDAO.insertRoutineEvent(routineEvent);
                for (DayOfWeek day : dayOfWeekList) {
                    day.setRoutineEventId(routineEvent.getId().intValue());
                    dayOfWeekDAO.insertDayOfWeek(day);
                }

            } else {
                routineEventDAO.updateRoutineEvent(routineEvent);
                for (DayOfWeek day : dayOfWeekList) {
                    dayOfWeekDAO.updateDayOfWeek(day, (int) id);
                }
            }
            setResult(RESULT_OK);
            finish();
        }
    }

    public void onSwitchClicked(View view) {
        if (notificationSpinner.getVisibility() == View.VISIBLE) {
            notificationSpinner.setVisibility(View.GONE);
        } else {
            notificationSpinner.setVisibility(View.VISIBLE);
        }
    }
}
