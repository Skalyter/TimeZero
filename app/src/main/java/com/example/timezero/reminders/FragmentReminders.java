package com.example.timezero.reminders;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timezero.MainActivity;
import com.example.timezero.R;
import com.example.timezero.database.ReminderDAO;
import com.example.timezero.model.Reminder;
import com.example.timezero.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class FragmentReminders extends Fragment {

    private LinearLayout linearMain, linearReminders;
    private ImageView reset;

    private List<Reminder> reminderList = new ArrayList<>();
    private ReminderListAdapter reminderListAdapter;
    private RecyclerView recyclerView;
    private ReminderDAO reminderDAO;

    private static final int EDIT_REMINDER_REQUEST_CODE = 100;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        reminderDAO = new ReminderDAO(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FloatingActionButton fab = ((MainActivity)getActivity()).findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AddEditReminderActivity.getIntent(getActivity(), null));
            }
        });
        return inflater.inflate(R.layout.fragment_reminders_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    @Override
    public void onResume() {
        super.onResume();
        reset = ((MainActivity)getActivity()).findViewById(R.id.resetDate);
        linearReminders = ((MainActivity)getActivity()).findViewById(R.id.navigation_reminder_layout);
        linearMain = ((MainActivity)getActivity()).findViewById(R.id.navigation_layout);
        linearMain.setVisibility(View.INVISIBLE);
        reset.setVisibility(View.INVISIBLE);
        linearReminders.setVisibility(View.VISIBLE);
        setList(reminderDAO.getAllReminders());
    }
    @Override
    public void onStop() {
        super.onStop();
        linearReminders.setVisibility(View.INVISIBLE);
        linearMain.setVisibility(View.VISIBLE);
        reset.setVisibility(View.VISIBLE);
    }

    private void showPopupMenu(View view, final int position){
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_reminders_popup, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_delete:
                        reminderDAO.deleteReminder(reminderList.get(position));
                        reminderList.remove(position);
                        reminderListAdapter.notifyItemRemoved(position);
                        reminderListAdapter.notifyItemRangeChanged(position, reminderList.size());
                        //setList(reminderDAO.getAllReminders());
                        Toast.makeText(getContext(), "Reminder deleted", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_edit:
                        Intent intent = AddEditReminderActivity.getIntent(getContext(), reminderList.get(position).getId());
                        startActivityForResult(intent, EDIT_REMINDER_REQUEST_CODE);
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    private void setList(List<Reminder> reminders){
        reminderList.clear();
        reminderList.addAll(reminders);

        if(reminderListAdapter == null){
            recyclerView = ((MainActivity)getActivity()).findViewById(R.id.reminder_list);
            reminderListAdapter = new ReminderListAdapter();

            RecyclerView.LayoutManager layoutManager
                    = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(reminderListAdapter);
        } else {
            reminderListAdapter.notifyDataSetChanged();
        }
    }

    public class ReminderListAdapter extends
            RecyclerView.Adapter<ReminderListAdapter.ReminderViewHolder>{
        @NonNull
        @Override
        public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.row_reminder_list, viewGroup, false);
            return new ReminderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ReminderViewHolder reminderViewHolder, final int position) {
            final Reminder reminder = reminderList.get(position);
            reminderViewHolder.reminderTitle.setText(reminder.getTitle());
            reminderViewHolder.reminderDescription.setText(reminder.getDescription());
            reminderViewHolder.reminderHour.setText(DateUtil.getHourFromDate(reminder.getStartDate()));
            reminderViewHolder.reminderMinute.setText(DateUtil.getMinuteFromDate(reminder.getStartDate()));
            reminderViewHolder.reminderDate.setText(DateUtil.getSimpleDate(reminder.getStartDate()));
            reminderViewHolder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupMenu(v, position);
                }
            });
            reminderViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(
                            ReminderDetailsActivity.getIntent(
                                    getContext(),
                                    reminderList.get(position).getId()));
                }
            });
        }

        @Override
        public int getItemCount() {
            return reminderList.size();
        }

        public class ReminderViewHolder extends RecyclerView.ViewHolder {

            public TextView reminderTitle, reminderDescription, reminderDate, reminderHour, reminderMinute;
            public ImageView more;
            public CardView cardView;
            public ReminderViewHolder(@NonNull View itemView) {
                super(itemView);
                reminderTitle = itemView.findViewById(R.id.reminder_item_title);
                reminderDescription = itemView.findViewById(R.id.reminder_item_description);
                reminderDate = itemView.findViewById(R.id.reminder_item_date);
                reminderHour = itemView.findViewById(R.id.reminder_item_hour);
                reminderMinute = itemView.findViewById(R.id.reminder_item_minute);
                more = itemView.findViewById(R.id.more_menu);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == EDIT_REMINDER_REQUEST_CODE
                && resultCode == RESULT_OK){
            setList(reminderDAO.getAllReminders());
        }
    }
}
