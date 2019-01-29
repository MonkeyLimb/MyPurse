package com.example.zachmarcelo.mypurse;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.zachmarcelo.mypurse.App.CHANNEL_1_ID;

/**
 * Created by Zach Marcelo on 12/8/2018.
 */

public class AddAlarmActivity extends AppCompatActivity implements View.OnClickListener,RecyclerAdapter.OnItemClickListener {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;
    private ValueEventListener mDBListener;
    DatabaseReference myRef ;
    String uID;
    List<SavedAlarms> AlarmList;
    RecyclerAdapter mAdapter;

    Calendar cal_alarm, cal_now;
    AlarmManager alarmManager;


    //widgets
    RecyclerView mRecyclerView;
    TextView no_reminder;
    FloatingActionButton fab;
    Dialog myDialog;
    Button save,cancel;
    TimePicker timePicker;
    NotificationManagerCompat notificationManager;
    EditText dia_edittext;
    String message, ev_message;
    int mHour, mMin;
    SwitchCompat _switch;
    boolean bool_repeat, bool_active;

    Long tim;

    private final long[] pattern = {100, 300, 300, 300};

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intenthome = new Intent(getApplicationContext(), HomeActivity.class);
                    intenthome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intenthome);
                    break;
                case R.id.navigation_measure:
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);

                    break;
                case R.id.navigation_alarm:
//                    Intent intent_alarm = new Intent(getApplicationContext(), AddAlarmActivity.class);
//                    startActivity(intent_alarm);

                    break;
                case R.id.navigation_calendar:
                    Intent intent2 = new Intent(getApplicationContext(), CalendarActivity.class);
                    startActivity(intent2);

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);setTheme(R.style.NoActionBar);
        setContentView(R.layout.activity_alarm);
        mRecyclerView = findViewById(R.id.recycler_view_list);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(AddAlarmActivity.this);
        mRecyclerView.setLayoutManager(layoutManager);
        fab = findViewById(R.id._fab);
        no_reminder = findViewById(R.id.no_reminder_text);




        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        if (mAuth.getCurrentUser() != null) {
            mUser = mAuth.getCurrentUser();
            uID = mUser.getUid();
        }
        AlarmList = new ArrayList<>();
        mAdapter = new RecyclerAdapter(getApplicationContext(), AlarmList);

        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(AddAlarmActivity.this);

        populateRecycler();




        findViewById(R.id._fab).setOnClickListener(this);



        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        //BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);


    }

    public void populateRecycler(){

        Calendar currentCal = Calendar.getInstance();


        myRef = mDatabase.getReference().child("Saved_Alarms").child(uID);
        mDBListener = myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                AlarmList.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    SavedAlarms upload = postSnapshot.getValue(SavedAlarms.class);
                    upload.setKey(postSnapshot.getKey());
                    AlarmList.add(upload);
                    tim = Long.valueOf(upload.getTimeinmilli());
                    ev_message = upload.getReminder();


                    while (tim <= currentCal.getTimeInMillis()){
                        myRef.child(upload.getKey()).child("timeinmilli").setValue(String.valueOf(tim + 86400000));
                        tim += 86400000;
                    }


                    sendBroadcast(tim, bool_repeat, AlarmList.size(), upload.getActive(), AlarmList.size(),ev_message);
                }

                if(AlarmList.size() != 0)
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



    public void showAlarmDialog(){
        myDialog = new Dialog(AddAlarmActivity.this);
        myDialog.setContentView(R.layout.dialog_alarm);
        dia_edittext = myDialog.findViewById(R.id.dialog_remind);
        save = myDialog.findViewById(R.id.dia_save);
        cancel = myDialog.findViewById(R.id.dia_cancel);
        timePicker = myDialog.findViewById(R.id.dia_timePicker);
        _switch = myDialog.findViewById(R.id.dialog_switch);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setAlarm();

        myDialog.findViewById    (R.id.dia_save)     .setOnClickListener(this);
        myDialog.findViewById    (R.id.dia_cancel)   .setOnClickListener(this);
        myDialog.show();

    }

    private void setAlarm() {
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hr, int min) {

                mHour = hr;
                mMin = min;

            }
        });
    }

    public void setTimer(){


        Date date = new Date();

        cal_alarm = Calendar.getInstance();
        cal_now = Calendar.getInstance();

        cal_now.setTime(date);
        cal_alarm.setTime(date);

        cal_alarm.set(Calendar.HOUR_OF_DAY, mHour);
        cal_alarm.set(Calendar.MINUTE,mMin);
        cal_alarm.set(Calendar.SECOND,0);

        if(cal_alarm.before(cal_now) || cal_alarm.equals(cal_now)){
            cal_alarm.add(Calendar.DATE,1);
        }
        AddAlarmToDatabase(cal_alarm.getTimeInMillis());


    }

    public void sendBroadcast(Long timeinmilli, boolean bool, int position, boolean active, int size, String message){
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        PendingIntent pendingIntent;
        ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();


        for (int i = 0; i < size; i++) {
            if(active) {
                if (bool) {
                    Intent intent = new Intent(AddAlarmActivity.this, MyBroadcastReceiver.class);
                    intent.putExtra("Alarm", message);
                    pendingIntent = PendingIntent.getBroadcast(AddAlarmActivity.this, position, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    assert alarmManager != null;
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, timeinmilli, AlarmManager.INTERVAL_DAY, pendingIntent);
                } else {
                    Intent intent = new Intent(AddAlarmActivity.this, MyBroadcastReceiver.class);
                    intent.putExtra("Alarm", message);
                    pendingIntent = PendingIntent.getBroadcast(AddAlarmActivity.this, position, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    assert alarmManager != null;
                    alarmManager.set(AlarmManager.RTC_WAKEUP, timeinmilli, pendingIntent);
                }
                intentArray.add(pendingIntent);
            }
            else {
                Intent intent = new Intent(AddAlarmActivity.this, MyBroadcastReceiver.class);
                intent.putExtra("Alarm", message);
                pendingIntent = PendingIntent.getBroadcast(AddAlarmActivity.this, position, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                assert alarmManager != null;
                alarmManager.cancel(pendingIntent);
            }
        }


//        sendOnChannel1();
    }

    public void sendOnChannel1() {


//        Intent activityIntent = new Intent(this, MainActivity.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(this,
//                0, activityIntent, 0);
//
//        Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
//        broadcastIntent.putExtra("toastMessage", message);
//        PendingIntent actionIntent = PendingIntent.getBroadcast(this,
//                0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
//                .setContentTitle("ALARM")
//                .setContentText(message)
//                .setSmallIcon(R.drawable.logo2)
//                .setPriority(Notification.PRIORITY_HIGH)
//                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                .setColor(Color.BLUE)
//                .setContentIntent(contentIntent)
//                .setAutoCancel(true)
//                .setOnlyAlertOnce(true)
//                .addAction(R.mipmap.ic_launcher, "Toast", actionIntent)
//                .build();
//
//        notificationManager.notify(1, notification);
    }

    public void AddAlarmToDatabase(Long timeinmilli){
        SavedAlarms Alarms = new SavedAlarms(
                String.valueOf(mHour),
                String.valueOf(mMin),
                timeinmilli.toString(),
                message,
                bool_repeat,
                true




                );

        FirebaseDatabase.getInstance().getReference("Saved_Alarms")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push()
                .setValue(Alarms).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                int i = String.valueOf(mMin).length();
                int x = String.valueOf(mHour).length();
//                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                if (task.isSuccessful()) {
//                    addEventInCalendar();
                    if( i == 1 ){

                        if( x == 1 )
                            Toast.makeText(getApplicationContext(), "Alarm set for 0" + mHour + ":0" + mMin + ".", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getApplicationContext(), "Alarm set for " + mHour + ":0" + mMin + ".", Toast.LENGTH_SHORT).show();

                    }
                    else{
                        if( x == 1 )
                            Toast.makeText(getApplicationContext(), "Alarm set for 0" + mHour + ":" + mMin + ".", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getApplicationContext(), "Alarm set for " + mHour + ":" + mMin + ".", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(AddAlarmActivity.this, getString(R.string.event_add_fail), Toast.LENGTH_LONG).show();
                }
            }


        });

    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id._fab:
                showAlarmDialog();

                break;

            case R.id.dia_save:
                message = dia_edittext.getText().toString();
                bool_repeat =_switch.isChecked();

                setTimer();
                myDialog.dismiss();






                break;

            case R.id.dia_cancel:
                myDialog.dismiss();

                break;
            default:
                break;
        }

    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onWhatEverClick(int position) {

    }

    @Override
    public void onDeleteClick(int position) {
        SavedAlarms selectedItem = AlarmList.get(position);
        String selectedKey = selectedItem.getKey();
        myRef.child(selectedKey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Reminder deleted", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myRef.removeEventListener(mDBListener);
    }
}
