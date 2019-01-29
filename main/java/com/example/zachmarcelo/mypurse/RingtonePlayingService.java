package com.example.zachmarcelo.mypurse;

import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Zach Marcelo on 12/11/2018.
 */

public class RingtonePlayingService extends Service {
    private Ringtone r;

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Uri ringtoneUri = Uri.parse(intent.getExtras().getString("ringtone-uri"));
        r = RingtoneManager.getRingtone(this,ringtoneUri);
        r.play();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
        r.stop();
    }
}
