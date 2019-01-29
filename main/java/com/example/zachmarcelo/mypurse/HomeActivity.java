package com.example.zachmarcelo.mypurse;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;


import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Zach Marcelo on 11/5/2018.
 */

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;
    private ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;
    private TextDrawable mDrawableBuilder;
    ImageView mThumbnailImage;


    CardView cv1,cv2,cv3,cv4;
    TextView dia_tv,dia_title,userrr;
    TextView rdia, rsys, rpp;
    Dialog myDialog;
    ImageView img;
    String uID;
    VideoView vidview;





    @Override
    public void onClick(View view) {
        myDialog = new Dialog(HomeActivity.this);
        myDialog.setContentView(R.layout.dialog_custom);
        dia_tv = myDialog.findViewById(R.id.dialog_TextView);
        dia_title = myDialog.findViewById(R.id.dialog_name_id);
        img = myDialog.findViewById(R.id.dialog_IV);
        String path = "";


        switch (view.getId()){
            case R.id.cv1:
                vidview = myDialog.findViewById(R.id.video_dialog);
                path = "android.resource://" + getPackageName() + "/" + R.raw.hypertension;
                vidview.setVideoPath(path);
                vidview.start();

                img.setImageDrawable(getResources().getDrawable(R.drawable.highmeter));
                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDialog.show();
                break;
            case R.id.cv2:
                vidview = myDialog.findViewById(R.id.video_dialog);
                path = "android.resource://" + getPackageName() + "/" + R.raw.hypotension;
                vidview.setVideoPath(path);
                vidview.start();

//                final Dialog myDialog = new Dialog(HomeActivity.this);
                img.setImageDrawable(getResources().getDrawable(R.drawable.lowmeter));
                dia_title.setText(R.string.firstaid2);
                dia_tv.setText(R.string.hypoinfo);
                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDialog.show();
                break;
            case R.id.cv3:

                vidview = myDialog.findViewById(R.id.video_dialog);
                path = "android.resource://" + getPackageName() + "/" + R.raw.heart_attack;
                vidview.setVideoPath(path);
                vidview.start();

//                final Dialog myDialog = new Dialog(HomeActivity.this);

                dia_title.setText(R.string.firstaid3);
                dia_tv.setText(R.string.heart_attackinfo);
                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDialog.show();
                break;
            case R.id.cv4:
                vidview = myDialog.findViewById(R.id.video_dialog);
                path = "android.resource://" + getPackageName() + "/" + R.raw.pulsepressure;
                vidview.setVideoPath(path);
                vidview.start();

                dia_title.setText(R.string.info1);
                dia_tv.setText(R.string.pulse_pressure_info);
                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDialog.show();
                break;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);setTheme(R.style.NoActionBar);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        if (mUser != null) {
            uID = mUser.getUid();
        }
        userrr = findViewById(R.id.user_welcome);
        rsys = findViewById(R.id.RecentSys);
        rdia = findViewById(R.id.RecentDia);
        rpp = findViewById(R.id.RecentPP);
        mThumbnailImage = findViewById(R.id.imageviewuser);


        cv1 = findViewById(R.id.cv1);
        cv2 = findViewById(R.id.cv2);
        cv3 = findViewById(R.id.cv3);
        cv4 = findViewById(R.id.cv4);


        getUserName();
        getCurrentBP();
//        userrr.setText(uID);


        findViewById(R.id.cv1).setOnClickListener(this);
        findViewById(R.id.cv2).setOnClickListener(this);
        findViewById(R.id.cv3).setOnClickListener(this);
        findViewById(R.id.cv4).setOnClickListener(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        //BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
    }

    public void setReminderTitle(String title) {

        String letter = null;

        if(title != null && !title.isEmpty()) {
            letter = title.substring(0, 1);
        }

        int color = mColorGenerator.getRandomColor();

        // Create a circular icon consisting of  a random background colour and first letter of title
        mDrawableBuilder = TextDrawable.builder()
                .buildRound(letter, color);
        mThumbnailImage.setImageDrawable(mDrawableBuilder);
    }

    protected void getUserName(){

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user != null) {
                    userrr.setText(String.format(getString(R.string.recent_stats_for_s), user.getName()));
                    setReminderTitle(user.getName());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        usersRef.child(uID).addListenerForSingleValueEvent(eventListener);

    }

    protected void getCurrentBP(){


        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference();
        String key = usersRef.child("All_Measure_Logs").child(uID).push().getKey();
        Query query = usersRef.child("All_Measure_Logs").child(uID).orderByChild(key).limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    BPMeasurement x = userSnapshot.getValue(BPMeasurement.class);
                    if(x!=null){
                        rsys.setText(x.getsystolic());
                        rdia.setText(x.getdiastolic());
                        rpp.setText(x.getPulse_pressure());
                    }
                    else
                        rsys.setText("FAILED");

                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    // Already at home
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
                    Intent intent2 = new Intent(getApplicationContext(), CalendarActivity.class);
                    startActivity(intent2);

                    break;
                case R.id.navigation_profile:
                    mAuth.signOut();
                    Intent intent3  = new Intent(com.example.zachmarcelo.mypurse.HomeActivity.this, LoginActivity.class);
                    intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent3);
                    finish();

                    break;
            }
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_log_out) {
            mAuth.signOut();
            Intent intent  = new Intent(com.example.zachmarcelo.mypurse.HomeActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
