package com.example.timezero.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.timezero.MainActivity;
import com.example.timezero.R;
import com.example.timezero.database.ActivityDAO;
import com.example.timezero.events.AddEditEventActivity;
import com.example.timezero.events.EventDetailsActivity;
import com.example.timezero.model.Activity;
import com.example.timezero.model.RoutineEvent;
import com.example.timezero.reminders.AddEditReminderActivity;
import com.example.timezero.reminders.ReminderDetailsActivity;
import com.example.timezero.routines.RoutineEventDetailsActivity;
import com.example.timezero.util.DateUtil;
import com.example.timezero.util.MaterialColors;

import java.util.ArrayList;
import java.util.List;

public class FragmentMain extends Fragment {
    private FloatingActionButton fab, fab_event, fab_reminder;
    private TextView twEvent, twReminder, today;
    private Animation fabOpen, fabClose, rotateForward, rotateBackward;
    private boolean isOpen = false;
    private View transparentLayout;

    private List<Activity> activityList = new ArrayList<>();
    private ActivityListAdapter activityListAdapter;
    private RecyclerView recyclerView;

    private ActivityDAO activityDAO;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activityDAO = new ActivityDAO(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fab = ((MainActivity) getActivity()).findViewById(R.id.fab);
        fab_event = ((MainActivity) getActivity()).findViewById(R.id.fab_event);
        fab_reminder = ((MainActivity) getActivity()).findViewById(R.id.fab_reminder);
        twEvent = ((MainActivity) getActivity()).findViewById(R.id.text_event);
        twReminder = ((MainActivity) getActivity()).findViewById(R.id.text_reminder);
        fabOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_backward);
        today = ((MainActivity) getActivity()).findViewById(R.id.today);

        today.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setList(activityDAO.getActivitiesByDate(DateUtil.getDate(today.getText().toString())));
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
                getActivity().findViewById(
                        R.id.transparent_layout).setVisibility(View.VISIBLE);
            }
        });
        fab_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AddEditEventActivity.getIntent(getActivity(), null));
                animateFab();
            }
        });
        fab_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AddEditReminderActivity.getIntent(getActivity(), null));
                animateFab();
            }
        });
        transparentLayout = getActivity().findViewById(R.id.transparent_layout);
        transparentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transparentLayout.setVisibility(View.GONE);
                if (isOpen)
                    animateFab();
            }
        });


        return inflater.inflate(R.layout.fragment_main_layout, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        setList(activityDAO.getActivitiesByDate(DateUtil.getDate(today.getText().toString())));
    }

    public void setList(List<Activity> activities) {
        activityList.clear();
        activityList.addAll(activities);
        if (activityListAdapter == null) {
            recyclerView = ((MainActivity) getActivity()).findViewById(R.id.activity_list);
            activityListAdapter = new ActivityListAdapter();

            RecyclerView.LayoutManager layoutManager
                    = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(activityListAdapter);
        } else {
            activityListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    public void onDayChanged(){
        setList(activityDAO.getActivitiesByDate(DateUtil.getDate(today.getText().toString())));
    }

    private void animateFab() {
        if (isOpen) {
            fab.startAnimation(rotateBackward);
            twEvent.startAnimation(fabClose);
            twReminder.startAnimation(fabClose);
            fab_event.startAnimation(fabClose);
            fab_reminder.startAnimation(fabClose);
            fab_event.setClickable(false);
            fab_reminder.setClickable(false);
            isOpen = false;
        } else {
            fab.startAnimation(rotateForward);
            twEvent.startAnimation(fabOpen);
            twReminder.startAnimation(fabOpen);
            fab_event.startAnimation(fabOpen);
            fab_reminder.startAnimation(fabOpen);
            fab_event.setClickable(true);
            fab_reminder.setClickable(true);
            isOpen = true;
        }
    }

    public class ActivityListAdapter
            extends RecyclerView.Adapter<ActivityListAdapter.ActivityViewHolder> {

        @NonNull
        @Override
        public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.row_activity_list, viewGroup, false);
            return new ActivityViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ActivityViewHolder activityViewHolder,
                                     final int position) {
            final Activity activity = activityList.get(position);
            Drawable image;
            if (activity.getType().equals("event")) {
                image = ContextCompat.getDrawable(getActivity(), R.drawable.ic_event_black_24dp);
            } else if (activity.getType().equals("reminder")) {
                image = ContextCompat.getDrawable(getActivity(), R.drawable.ic_notifications_active_black_24dp);
            } else {
                image = ContextCompat.getDrawable(getActivity(), R.drawable.ic_repeat_black_24dp);
            }
            activityViewHolder.icon.setBackground(image);
            activityViewHolder.activityMinute.setText(
                    DateUtil.getMinuteFromDate(
                            activity.getStartDate()));
            activityViewHolder.activityHour.setText(
                    DateUtil.getHourFromDate(
                            activity.getStartDate()));
            activityViewHolder.activityName.setText(activity.getTitle());
            if (activity.getDescription() != null && !activity.getDescription().equals("")) {
                activityViewHolder.activityDescription.setText(activity.getDescription());
                activityViewHolder.activityDescription.setTextColor(MaterialColors.BLACK);
            } else {
                activityViewHolder.activityDescription.setText(getString(R.string.no_description));
                //set color to darker gray
                activityViewHolder.activityDescription.setTextColor(0xffaaaaaa);
            }
            activityViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (activity.getType()) {
                        case "event":

                            startActivity(EventDetailsActivity
                                    .getIntent(getContext(), activity.getId()));

                            break;
                        case "reminder":

                            startActivity(ReminderDetailsActivity
                                    .getIntent(getContext(), activity.getId()));

                            break;
                        case "routine_event":
                            RoutineEvent event = (RoutineEvent) activity;
                            startActivity(RoutineEventDetailsActivity
                                    .getIntent(getContext(), event.getId()));
                            break;
                    }
                }
            });
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    notifyItemChanged(activityViewHolder
                            .getAdapterPosition());
                }
            });
        }

        @Override
        public int getItemCount() {
            return activityList.size();
        }

        public class ActivityViewHolder extends RecyclerView.ViewHolder {

            public TextView activityName, activityDescription, activityHour, activityMinute;
            public CardView item;
            public ImageView icon;

            public ActivityViewHolder(@NonNull View itemView) {
                super(itemView);
                item = itemView.findViewById(R.id.activity_list_item);
                icon = itemView.findViewById(R.id.activity_icon);
                activityName = itemView.findViewById(R.id.activity_title);
                activityDescription = itemView.findViewById(R.id.activity_description);
                activityHour = itemView.findViewById(R.id.activity_hour);
                activityMinute = itemView.findViewById(R.id.activity_minute);
            }
        }
    }
}
