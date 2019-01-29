package com.example.zachmarcelo.mypurse;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Random;

/**
 * Created by Zach Marcelo on 11/16/2018.
 */

public class MeasureActivity extends AppCompatActivity implements View.OnClickListener {
    TextView sys, dia, pp, measurement;
    Button  measure,save,cancel;
    ProgressDialog progress;
    Dialog myDialog;
    String a,b,c;
    long date;

    FirebaseAuth mAuth;

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
//                    Intent intent1  = new Intent(getApplicationContext(), MeasureActivity.class);
//                    startActivity(intent1);

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
    protected void onCreate(@Nullable Bundle savedInstanceState)    {
        super.onCreate(savedInstanceState);setTheme(R.style.NoActionBar);
        setContentView(R.layout.activity_measure);
        mAuth = FirebaseAuth.getInstance();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        //BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        String strDate = sdf.format(date);
        SimpleDateFormat sdf1 = new SimpleDateFormat(" hh:mm:ss a");
        String strTime = sdf1.format(date);

        sys     =   findViewById(R.id.systolic);
        dia     =   findViewById(R.id.diastolic);
        pp      =   findViewById(R.id.pp);
        measure =   findViewById(R.id.measure);

        findViewById    (R.id.measure)  .setOnClickListener(this);
        findViewById    (R.id.clear)    .setOnClickListener(this);

    }

    public void registerMeasurement() {

        BPMeasurement bp = new BPMeasurement(
                sys.getText().toString(),
                dia.getText().toString(),
                pp.getText().toString(),
                String.valueOf(date)
        );

        FirebaseDatabase.getInstance().getReference("Saved_Measurement_UserChoice")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push()
                .setValue(bp).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

//                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                if (task.isSuccessful()) {
                    addEventInCalendar();
                    Toast.makeText(MeasureActivity.this, getString(R.string.ev_saved), Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(MeasureActivity.this, getString(R.string.event_add_fail), Toast.LENGTH_LONG).show();
                }
            }


        });



        myDialog.dismiss();
    }

    public void addEventInCalendar() {

    }

    public void getMeasurementss(){
        final int random = new Random().nextInt(81-20) + 20;
        final int random1 = new Random().nextInt(151-100) + 100;
        final int diff = random1 - random;
        sys.setText(String.valueOf(random1));
        dia.setText (String.valueOf(random));
        pp. setText (String.valueOf(diff));

        registerMeasurementasLog();
    }

    public void delay5secs(){

        final Handler handler = new Handler();
        handler.postDelayed(this::dismissDia, 5000);

    }

    void dismissDia(){
        progress.dismiss();
    }

    public void registerMeasurementasLog(){

        BPMeasurement bp = new BPMeasurement(
                sys.getText().toString(),
                dia.getText().toString(),
                pp.getText().toString(),
                String.valueOf(date)
        );

        FirebaseDatabase.getInstance().getReference("All_Measure_Logs")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push()
                .setValue(bp);

    }

    public void showDia(){

        myDialog = new Dialog(MeasureActivity.this);
        myDialog.setContentView(R.layout.dialog_confirmation);
        save = myDialog.findViewById(R.id.save_button);
        cancel = myDialog.findViewById(R.id.cancel_action);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        measurement = myDialog.findViewById(R.id.confirmation_text);
        measurement.setText(String.format("Save measurement: %s over %s with Pulse pressure of %s?", sys.getText(), dia.getText(), pp.getText()));


        myDialog.findViewById    (R.id.save_button)     .setOnClickListener(this);
        myDialog.findViewById    (R.id.cancel_action)   .setOnClickListener(this);
        myDialog.show();

    }

    @Override
    public void onClick(View view) {
        final Handler handler1 = new Handler();
        switch (view.getId()){
            case R.id.measure:


                if(measure.getText().toString().equals("Measure")){
                    progress = ProgressDialog.show(this, "",
                            "Please wait...", false);

                    delay5secs();
                    handler1.postDelayed(this::getMeasurementss,4999);
                    measure.setText(getString(R.string.calendar_save));
                }

                else if(measure.getText().toString().equals(getString(R.string.calendar_save))){
                    showDia();
                }



                break;
            case R.id.clear:
                    sys .setText("--");
                    dia .setText("--");
                    pp  .setText("--");
                    if(measure.getText().toString().equals(getString(R.string.calendar_save))){
                        measure.setText(getString(R.string.measure));
                    }
                break;

            case R.id.save_button:
                registerMeasurement();

                break;

            case R.id.cancel_action:
                myDialog.dismiss();

                break;

            default:
                break;
        }
    }

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
            Intent intent  = new Intent(com.example.zachmarcelo.mypurse.MeasureActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
