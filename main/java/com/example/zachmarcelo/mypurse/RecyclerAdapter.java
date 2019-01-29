package com.example.zachmarcelo.mypurse;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by Zach Marcelo on 12/12/2018.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>  {


    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;
    private ValueEventListener mDBListener;
    DatabaseReference myRef ;
    private Context mContext;
    private List<SavedAlarms> mAlarms;
    private OnItemClickListener mListener;
    String uID;

    private Context context;
    private String res;




    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public TextView title, time, repeat;
        public Switch switchCompat;
        final int pos = getAdapterPosition();



        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.recycle_title);
            time = (TextView) view.findViewById(R.id.recycle_date_time);
            repeat = (TextView) view.findViewById(R.id.recycle_repeat_info);
            switchCompat = view.findViewById(R.id.active_switch);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {

                    switch (item.getItemId()) {
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
    public long getItemId(int position) {
        return position;
    }


    public RecyclerAdapter(Context context, List<SavedAlarms> list) {
        mAlarms = list;
        mContext = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.cardview_items,parent,false);

        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SavedAlarms savedAlarms = mAlarms.get(position);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();


        if (mAuth.getCurrentUser() != null) {
            mUser = mAuth.getCurrentUser();
            uID = mUser.getUid();
        }

        SavedAlarms mylist = mAlarms.get(position);
        holder.title.setText(mylist.getReminder());
        holder.time.setText(mylist.gethour() + ":" +mylist.getminute());

        if(mylist.getRepeat())
            holder.repeat.setText(R.string.rep_on);
        else
            holder.repeat.setText(R.string.rep_off);

        holder.switchCompat.setChecked(mylist.getActive());

        holder.switchCompat.setOnCheckedChangeListener((compoundButton, b) -> {
            context = compoundButton.getContext();

        myRef = FirebaseDatabase.getInstance().getReference().child("Saved_Alarms").child(uID);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    res = userSnapshot.getKey();
                  Query query = myRef.orderByKey().equalTo(mAlarms.get(position).getKey());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()){
//                                SavedAlarms upload = userSnapshot.getValue(SavedAlarms.class);
                                myRef.child(userSnapshot.getKey()).child("active").setValue(b);

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        });

    }

    @Override
    public int getItemCount() {
        return mAlarms.size();
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
