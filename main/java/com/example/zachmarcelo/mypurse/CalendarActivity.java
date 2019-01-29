package com.example.zachmarcelo.mypurse;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.EventDay;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Zach Marcelo on 11/23/2018.
 */

public class CalendarActivity extends AppCompatActivity implements View.OnClickListener, CalendarRecyclerAdapter.OnItemClickListener {

    public static final String RESULT = "result";
    public static final String EVENT = "event";
    private static final int ADD_NOTE = 44;
    Long getdate;
    CalendarRecyclerAdapter mAdapter;

    TextView no_reminder;
    RecyclerView mRecyclerView;


    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;
    private List<BPMeasurement> mEventDays;

    private ValueEventListener mDBListener;
    DatabaseReference myRef ;

    String uID,x,xx,xxx;

    CalendarView mCalendarView;

    @Override
    protected void onStart() {
        super.onStart();

//        if (mAuth.getCurrentUser() != null) {
//            mUser = mAuth.getCurrentUser();
//        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.NoActionBar);
        setContentView(R.layout.activity_calendar);

        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            mUser = mAuth.getCurrentUser();
        }

        uID = mUser.getUid();

        long date = System.currentTimeMillis();
        SimpleDateFormat m = new SimpleDateFormat("MM");
        SimpleDateFormat d = new SimpleDateFormat("dd");
        SimpleDateFormat y = new SimpleDateFormat("yy");




        mRecyclerView = findViewById(R.id.RView);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(CalendarActivity.this);
        mRecyclerView.setLayoutManager(layoutManager);
        mCalendarView = findViewById(R.id.calendarView);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        //BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);

        mEventDays = new ArrayList<>();

        mAdapter = new CalendarRecyclerAdapter(getApplicationContext(), mEventDays);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(CalendarActivity.this);


        getdate = mCalendarView.getDate();
        no_reminder = findViewById(R.id.no_reminder_cal);

        x =m.format(date);
        xx =d.format(date);
        xxx =y.format(date);
        populateCalendarRecycler(xxx,x,x);


        final String[] j = new String[1];
        final String[] i = new String[1];
        final String[] v = new String[1];
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {

                if (year < 10)
                    j[0] = "0" + String.valueOf(year);
                else
                    j[0] = String.valueOf(year);

                if (month + 1 < 10)
                    i[0] = "0" + String.valueOf(month + 1);
                else
                    i[0] = String.valueOf(month + 1);

                if (dayOfMonth < 10)
                    v[0] = "0" + String.valueOf(dayOfMonth);
                else
                    v[0] = String.valueOf(dayOfMonth);

//                Toast.makeText(calendarView.getContext(), "Year=" + year + " Month=" + month + " Day=" + dayOfMonth, Toast.LENGTH_LONG).show();
                populateCalendarRecycler(j[0],i[0],v[0]);


            }
        });

    }

    private void populateCalendarRecycler(String year, String month,  String day) {

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        final Date[] d8 = {null};

        myRef = mDatabase.getReference().child("Saved_Measurement_UserChoice").child(uID);
        mDBListener = myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mEventDays.clear();


                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BPMeasurement upload = postSnapshot.getValue(BPMeasurement.class);
                    upload.setKey(postSnapshot.getKey());

                    if (upload != null) {

                        String strDate = sdf.format(Double.valueOf(upload.getTimeinmilli()));
                        if (strDate.equals(month + "/" + day + "/" + year)) {
                            mEventDays.add(upload);
                        }
                        Log.e("ERRR",month + "/" + day + "/" + year );
                        Log.e("re", strDate);
                    }

                }

                if(mEventDays.size() != 0)
                    no_reminder.setVisibility(View.GONE);
                else
                    no_reminder.setVisibility(View.VISIBLE);

                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onWhatEverClick(int position) {

    }

    @Override
    public void onDeleteClick(int position) {
        BPMeasurement selectedItem = mEventDays.get(position);
        String selectedKey = selectedItem.getKey();
        myRef.child(selectedKey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Reminder deleted", Toast.LENGTH_SHORT).show();
            }
        });

    }



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intent0 = new Intent(getApplicationContext(), HomeActivity.class);
                    intent0.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent0);
                    break;
                case R.id.navigation_measure:
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);

                    break;
                case R.id.navigation_alarm:
                    Intent intent_alarm = new Intent(getApplicationContext(), AddAlarmActivity.class);
                    startActivity(intent_alarm);

                    break;
                case R.id.navigation_calendar:
//                    Intent intent2 = new Intent(getApplicationContext(), CalendarActivity.class);
//                    startActivity(intent2);
//already here
                    break;
                case R.id.navigation_profile:
                    mAuth.signOut();
                    Intent intent3  = new Intent(getApplicationContext(), LoginActivity.class);
                    intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent3);
                    finish();

                    break;
            }
            return false;
        }
    };

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myRef.removeEventListener(mDBListener);
    }

}
