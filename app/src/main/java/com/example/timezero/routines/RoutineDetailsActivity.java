package com.example.timezero.routines;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.timezero.R;
import com.example.timezero.database.RoutineDAO;
import com.example.timezero.database.RoutineEventDAO;
import com.example.timezero.model.HeaderItem;
import com.example.timezero.model.ListItem;
import com.example.timezero.model.Routine;
import com.example.timezero.model.RoutineEvent;
import com.example.timezero.model.RoutineEventItem;
import com.example.timezero.util.DateUtil;
import com.example.timezero.util.IntentConstants;

import java.util.ArrayList;
import java.util.List;


public class RoutineDetailsActivity extends AppCompatActivity {

    private static final int EDIT_EVENT_REQUEST_CODE = 100;

    private RoutineEventDAO routineEventDAO;
    private Long id;
    private Long routineId;

    private RoutineDAO routineDAO;

    List<ListItem> mItems;
    RecyclerView recyclerView;
    RoutineEventsAdapter adapter;

    public static Intent getIntent(Context context, Long id) {
        Intent intent = new Intent(context, RoutineDetailsActivity.class);
        intent.putExtra(IntentConstants.ID, id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        id=getIntent().getLongExtra(IntentConstants.ID, -1);
        routineId = getIntent().getLongExtra(IntentConstants.ROUTINE_ID,-1);
        routineDAO = new RoutineDAO(this);
        Routine routine = routineDAO.getRoutineByID(id);
        getSupportActionBar().setTitle(routine.getTitle() + " details");
        routineEventDAO = new RoutineEventDAO(this);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(AddEditRoutineEventActivity.getIntent(RoutineDetailsActivity.this, null, id ));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setList();
        TextView intro = findViewById(R.id.routine_event_intro);
        if(mItems.size()>8){
            intro.setVisibility(View.GONE);
        } else {
            intro.setVisibility(View.VISIBLE);
        }
    }

    private void setList(){
        mItems = new ArrayList<>();
        for(int i=1;i<=7; i++){
            HeaderItem header = new HeaderItem();
            header.setHeader(DateUtil.getDayOfWeek(i));
            mItems.add(header);
            List<RoutineEvent> routineEvents = new ArrayList<>();
            routineEvents.addAll(routineEventDAO.getRoutineEventsForRoutine(id, i));
            List<RoutineEventItem> routineEventItems = new ArrayList<>();
            for (RoutineEvent event: routineEvents) {
                RoutineEventItem item = new RoutineEventItem();
                item.setRoutineEvent(event);
                routineEventItems.add(item);
            }
            mItems.addAll(routineEventItems);
            if(i==7){
                HeaderItem headerEnd = new HeaderItem();
                headerEnd.setHeader("");
                mItems.add(headerEnd);
            }
        }
        recyclerView = findViewById(R.id.routine_event_list);
        adapter = new RoutineEventsAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL,
                false));
        recyclerView.setAdapter(adapter);
    }


    private void showPopupMenu(View view, final int position){
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_routine_event_popup, popupMenu.getMenu());
        final RoutineEventItem routineEventItem = (RoutineEventItem)mItems.get(position);
        final long id = routineEventItem.getRoutineEvent().getId();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_delete:
                        routineEventDAO.deleteRoutineEvent(routineEventDAO.getRoutineEventByID(id));
                        setList();
                        break;
                    case R.id.menu_edit:

                        Intent intent
                                = AddEditRoutineEventActivity
                                .getIntent(RoutineDetailsActivity.this,
                                        id,
                                        routineId);
                        startActivityForResult(intent, EDIT_EVENT_REQUEST_CODE);
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == EDIT_EVENT_REQUEST_CODE && resultCode == RESULT_OK){
            setList();
        }
    }

    public class RoutineEventsAdapter
            extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


        public class HeaderViewHolder extends RecyclerView.ViewHolder{

            public TextView dayText;
            public HeaderViewHolder(@NonNull View itemView) {
                super(itemView);
                dayText = itemView.findViewById(R.id.day_of_week);
            }
        }

        public class RoutineEventViewHolder extends  RecyclerView.ViewHolder{

            public TextView start, end, title;
            public ImageView more;
            public CardView cardView;
            public RoutineEventViewHolder(@NonNull View itemView) {
                super(itemView);
                start = itemView.findViewById(R.id.routine_event_start);
                end = itemView.findViewById(R.id.routine_event_end);
                title = itemView.findViewById(R.id.routine_event_title);
                more = itemView.findViewById(R.id.routine_event_more_menu);
                cardView = itemView.findViewById(R.id.card_view);
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            if (getItemViewType(i) == ListItem.TYPE_HEADER) {
                View itemView = getLayoutInflater().inflate(R.layout.row_routine_day_list, viewGroup, false);
                return new RoutineEventsAdapter.HeaderViewHolder(itemView);
            } else {
                View itemView = getLayoutInflater().inflate(R.layout.row_routine_event_list, viewGroup, false);
                return new RoutineEventViewHolder(itemView);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            int type = getItemViewType(position);
            if (type == ListItem.TYPE_HEADER) {
                HeaderItem item = (HeaderItem) mItems.get(position);
                HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
                // your logic here
                holder.dayText.setText(item.getHeader());
            } else {
                final RoutineEventItem event = (RoutineEventItem) mItems.get(position);
                RoutineEventViewHolder holder = (RoutineEventViewHolder) viewHolder;
                // your logic here
                holder.start.setText(DateUtil.getStringTimeFromDate(event.getRoutineEvent().getStartDate()));
                holder.end.setText(DateUtil.getStringTimeFromDate(event.getRoutineEvent().getEndTime()));
                holder.title.setText(event.getRoutineEvent().getTitle());
                final int pos = position;
                holder.more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPopupMenu(v,pos);
                    }
                });
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(RoutineEventDetailsActivity
                                .getIntent(RoutineDetailsActivity.this,
                                        event.getRoutineEvent().getId()));
                    }
                });
            }
        }

        @Override
        public int getItemViewType(int position) {
            return mItems.get(position).getType();
        }

        @Override
        public int getItemCount() {
            if(mItems.size()==8){
                return 0;
            }
            return mItems.size();
        }
    }
}
