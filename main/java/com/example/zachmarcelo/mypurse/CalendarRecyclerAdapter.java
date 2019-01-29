package com.example.zachmarcelo.mypurse;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Zach Marcelo on 1/17/2019.
 */

public class CalendarRecyclerAdapter extends RecyclerView.Adapter<CalendarRecyclerAdapter.MyViewHolder>  {


    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;
    private ValueEventListener mDBListener;
    DatabaseReference myRef ;
    private Context mContext;
    private List<BPMeasurement> mCalendarEvents;
    private OnItemClickListener mListener;
    String uID;



    private Context context;
    private String res;

    public CalendarRecyclerAdapter(Context context, List<BPMeasurement> list) {
        mCalendarEvents = list;
        mContext = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cardview_calendar,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();


        if (mAuth.getCurrentUser() != null) {
            mUser = mAuth.getCurrentUser();
            uID = mUser.getUid();
        }


        BPMeasurement mylist = mCalendarEvents.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        String strDate = sdf.format(Double.valueOf(mylist.getTimeinmilli()));
        SimpleDateFormat sdf1 = new SimpleDateFormat(" hh:mm:ss a");
        String strTime = sdf1.format(Double.valueOf(mylist.getTimeinmilli()));

        holder.bp.setText(mylist.getsystolic() + " over " + mylist.getdiastolic());
        holder.pp.setText(mylist.getPulse_pressure());
        holder.time.setText(strDate + " " + strTime);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public TextView bp, time, pp;

        public MyViewHolder(View itemView) {
            super(itemView);
            bp = (TextView) itemView.findViewById(R.id.cal_bp);
            pp = (TextView) itemView.findViewById(R.id.cal_pp);
            time = (TextView) itemView.findViewById(R.id.cal_date_time);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {

                    switch (menuItem.getItemId()) {
                        case 1:
                            mListener.onWhatEverClick(position);
                            return true;
                        case 2:
                            mListener.onDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void onClick(View view) {

        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Select Action");

            MenuItem doWhatever = contextMenu.add(Menu.NONE, 1, 1, "Do something");
            MenuItem delete = contextMenu.add(Menu.NONE, 2, 2, "Delete");

            doWhatever.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);

        }
    }

    @Override
    public int getItemCount() {
        return mCalendarEvents.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onWhatEverClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
