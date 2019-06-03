package com.example.timezero.routines;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timezero.MainActivity;
import com.example.timezero.R;
import com.example.timezero.database.RoutineDAO;
import com.example.timezero.database.RoutineEventDAO;
import com.example.timezero.model.Routine;

import java.util.ArrayList;
import java.util.List;

public class FragmentRoutines extends Fragment {


    private LinearLayout linearMain, linearReminders;
    private ImageView reset;
    TextView routines;

    private List<Routine> routineList = new ArrayList<>();
    private RoutineListAdapter routineListAdapter;
    private RecyclerView recyclerView;
    private RoutineDAO routineDAO;

    public static long routineId = -1;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        routineDAO = new RoutineDAO(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FloatingActionButton fab = ((MainActivity)getActivity()).findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRoutineId(-1);
                Toast.makeText(getActivity(), "Add a new routine", Toast.LENGTH_SHORT).show();
                ShowDialogFragment();
            }
        });
        return inflater.inflate(R.layout.fragment_routines_layout, container, false);
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
        routines = ((MainActivity)getActivity()).findViewById(R.id.reminder_text_nav);
        routines.setText("Routines");
        linearMain.setVisibility(View.INVISIBLE);
        reset.setVisibility(View.INVISIBLE);
        linearReminders.setVisibility(View.VISIBLE);
        setList(routineDAO.getAllRoutines());
    }
    @Override
    public void onStop() {
        super.onStop();
        routines.setText("Reminders");
        linearReminders.setVisibility(View.INVISIBLE);
        linearMain.setVisibility(View.VISIBLE);
        reset.setVisibility(View.VISIBLE);
    }

    private void setList(List<Routine> routines){
        routineList.clear();
        routineList.addAll(routines);

        if(routineListAdapter == null){
            recyclerView = ((MainActivity)getActivity())
                    .findViewById(R.id.routine_list);
            routineListAdapter = new RoutineListAdapter();

            RecyclerView.LayoutManager layoutManager
                    = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(routineListAdapter);
        } else {
            routineListAdapter.notifyDataSetChanged();
        }
    }

    public void ShowDialogFragment(){
        FragmentAddEditRoutine dialogFragment = new FragmentAddEditRoutine();
        dialogFragment.setOnDialogResult(new FragmentAddEditRoutine.OnDialogResult() {
            @Override
            public void onDialogOK() {
                setList(routineDAO.getAllRoutines());
            }

            @Override
            public void onDialogCanceled() {
            }
        });
        dialogFragment.show(getFragmentManager(), "add_routine");
    }

    private void showPopupMenu(View view, final int position){
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_routines_popup, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_delete_routine:
                        routineDAO.deleteRoutine(routineList.get(position));
                        RoutineEventDAO routineEventDAO = new RoutineEventDAO(getContext());
                        routineEventDAO.deleteRoutineEventsForRoutine(routineList.get(position).getId());
                        setList(routineDAO.getAllRoutines());
                        Toast.makeText(getContext(), "Routine deleted", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.menu_rename_routine:
                        Toast.makeText(getContext(), "Rename routine", Toast.LENGTH_SHORT).show();
                        setRoutineId(position);
                        ShowDialogFragment();
                        break;
                    }
                    return true;
            }
        });
        popupMenu.show();
    }

    public void setRoutineId(int position){
        if(position==-1){
            routineId = position;
        } else {
            routineId = routineList.get(position).getId();
        }
    }

    public static long getRoutineId(){
        return routineId;
    }
    public class RoutineListAdapter extends
            RecyclerView.Adapter<RoutineListAdapter.RoutineViewHolder>{

        @NonNull
        @Override
        public RoutineViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.row_routine_list, viewGroup, false);
            return new RoutineViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RoutineViewHolder routineViewHolder,
                                     final int position) {
            final Routine routine = routineList.get(position);
            routineViewHolder.routineTitle.setText(routine.getTitle());
            if(routine.isActive()){
                routineViewHolder.routineStatusSwitch.setChecked(true);
                routineViewHolder.routineStatusSwitch.setText("Active");
            } else {
                routineViewHolder.routineStatusSwitch.setChecked(false);
                routineViewHolder.routineStatusSwitch.setText("Inactive");
            }
            routineViewHolder.routineStatusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    routine.setActive(isChecked);
                    if(isChecked){
                        routineViewHolder.routineStatusSwitch.setText("Active");
                    } else {
                        routineViewHolder.routineStatusSwitch.setText("Inactive");
                    }
                    routineDAO.updateRoutine(routine);
                }
            });
            routineViewHolder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupMenu(routineViewHolder.more, position);
                }
            });
            routineViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(RoutineDetailsActivity
                            .getIntent(getActivity(),
                                    routine.getId()));
                }
            });
        }

        @Override
        public int getItemCount() {
            return routineList.size();
        }

        public class RoutineViewHolder extends RecyclerView.ViewHolder{

            public TextView routineTitle;
            public Switch routineStatusSwitch;
            public ImageView more;

            public RoutineViewHolder(@NonNull View itemView) {
                super(itemView);
                routineTitle = itemView.findViewById(R.id.routine_name_item);
                routineStatusSwitch = itemView.findViewById(R.id.routine_switch_item);
                more = itemView.findViewById(R.id.routine_more_menu_item);
            }
        }
    }
}
