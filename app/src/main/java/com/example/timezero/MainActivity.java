package com.example.timezero;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.timezero.activities.AboutActivity;
import com.example.timezero.activities.HelpActivity;
import com.example.timezero.activities.SettingsActivity;
import com.example.timezero.events.FragmentEvents;
import com.example.timezero.fragments.FragmentMain;
import com.example.timezero.reminders.FragmentReminders;
import com.example.timezero.routines.FragmentRoutines;

import java.util.Calendar;


import static com.example.timezero.util.DateUtil.decreaseDate;
import static com.example.timezero.util.DateUtil.getTodayString;
import static com.example.timezero.util.DateUtil.increaseDate;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Calendar calendar = Calendar.getInstance();
    private DrawerLayout drawerLayout;
    private ImageView previous, next, resetDate;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TextView today;

    private FragmentMain fragmentMain;

    private Animation rotateReset;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ShowFragment(R.id.nav_home);
        fragmentMain = new FragmentMain();

        rotateReset = AnimationUtils.loadAnimation(this, R.anim.reset_rotate_backward);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);

        //Calendar manipulation
        today = findViewById(R.id.today);
        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        MainActivity.this,
                        android.R.style.Theme_DeviceDefault_Dialog,
                        dateSetListener,
                        year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                setTodayText(calendar);
            }
        };
        previous = findViewById(R.id.previous);
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseDate(calendar);
                setTodayText(calendar);
            }
        });
        next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseDate(calendar);
                setTodayText(calendar);
            }
        });
        resetDate = findViewById(R.id.resetDate);
        resetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetDate.startAnimation(rotateReset);
                calendar = Calendar.getInstance();
                setTodayText(calendar);
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        ShowFragment(item.getItemId());
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void setTodayText(Calendar calendar) {
        today.setText(getTodayString(calendar));
    }

    private void ShowFragment(int itemId) {

        Fragment fragment=null;

        switch (itemId) {
            case R.id.nav_home:
                fragment = new FragmentMain();
                break;
            case R.id.nav_events:
                fragment = new FragmentEvents();
                break;
            case R.id.nav_reminders:
                fragment = new FragmentReminders();
                break;
            case R.id.nav_routines:
                fragment = new FragmentRoutines();
                break;
            case R.id.nav_settings:
                startActivity(SettingsActivity.getIntent(this));
                break;
            case R.id.nav_help:
                startActivity(HelpActivity.getIntent(this));
                break;
            case R.id.nav_about:
                startActivity(AboutActivity.getIntent(this));
                break;
            default:
                fragment = null;
        }

        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment);
            fragmentTransaction.commit();
        }
    }

    public void onDayChanged(View view) {
        fragmentMain.onDayChanged();
    }
}