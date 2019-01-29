package com.example.zachmarcelo.mypurse;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Zach Marcelo on 12/11/2018.
 */

public class StopRingtone extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ringtoneStop();
    }

    public void ringtoneStop(){
        Intent stopIntent = new Intent(this, RingtonePlayingService.class);
        this.stopService(stopIntent);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
