package com.example.timezero.events;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timezero.MainActivity;
import com.example.timezero.R;
import com.example.timezero.database.EventDAO;
import com.example.timezero.model.Event;
import com.example.timezero.util.DateUtil;
import com.example.timezero.util.MaterialColors;

import java.util.ArrayList;
import java.util.List;

public class FragmentEvents extends Fragment {

    private TextView today;
    private List<Event> eventList = new ArrayList<>();
    private EventListAdapter eventListAdapter;
    private RecyclerView recyclerView;
    private EventDAO eventDAO;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FloatingActionButton fab = ((MainActivity) getActivity()).findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AddEditEventActivity.getIntent(getActivity(), null));
            }
        });
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
                setList(eventDAO.getEventsByDate(
                        DateUtil.getDate(today.getText().toString())));
            }
        });
        return inflater.inflate(R.layout.fragment_main_layout, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        eventDAO = new EventDAO(getActivity());

    }

    @Override
    public void onResume() {
        super.onResume();
        setList(eventDAO.getEventsByDate(DateUtil.getDate(today.getText().toString())));
    }

    private void setList(List<Event> events) {
        eventList.clear();
        eventList.addAll(events);

        if (eventListAdapter == null) {
            recyclerView = ((MainActivity) getActivity()).findViewById(R.id.activity_list);
            eventListAdapter = new EventListAdapter();

            RecyclerView.LayoutManager layoutManager
                    = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(eventListAdapter);
        } else {
            eventListAdapter.notifyDataSetChanged();
        }
    }

    public class EventListAdapter extends
            RecyclerView.Adapter<EventListAdapter.EventViewHolder> {

        @NonNull
        @Override
        public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.row_activity_list, viewGroup, false);
            return new EventViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull EventListAdapter.EventViewHolder eventViewHolder,
                                     int position) {
            final Event event = eventList.get(position);
            eventViewHolder.eventName.setText(event.getTitle());
            if (eventViewHolder.eventDescription != null &&
                    !eventViewHolder.eventDescription.toString().equals("")){
                eventViewHolder.eventDescription.setTextColor(MaterialColors.BLACK);
                eventViewHolder.eventDescription.setText(event.getDescription());
            }
            eventViewHolder.eventHour.setText(DateUtil.getHourFromDate(
                    event.getStartDate()));
            eventViewHolder.eventMinute.setText(DateUtil.getMinuteFromDate(
                    event.getStartDate()));
            eventViewHolder.itemView
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                               startActivity(
                                       EventDetailsActivity
                                           .getIntent(getActivity(),event.getId()));
                        }
                    });
        }

        @Override
        public int getItemCount() {
            return eventList.size();
        }

        public class EventViewHolder extends RecyclerView.ViewHolder {

            public TextView eventName, eventDescription, eventHour, eventMinute;

            public EventViewHolder(@NonNull View itemView) {
                super(itemView);
                eventName = itemView.findViewById(R.id.activity_title);
                eventDescription = itemView.findViewById(R.id.activity_description);
                eventHour = itemView.findViewById(R.id.activity_hour);
                eventMinute = itemView.findViewById(R.id.activity_minute);
            }
        }
    }
}
