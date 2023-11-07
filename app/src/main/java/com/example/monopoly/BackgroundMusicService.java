package com.example.monopoly;

import static com.example.monopoly.Game.ACTION_STOP_BACKGROUND_MUSIC;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class BackgroundMusicService extends Service {
    private MediaPlayer mediaPlayer;
    private BroadcastReceiver stopMusicReceiver = new BroadcastReceiver() {//broadcastreceiver use as listener to intent that sended to him by the system or other application
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_STOP_BACKGROUND_MUSIC)) {//check if the broadcast sended is action stop background music
                stopSelf();//stop the music
            }
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.backgroundmusic);
        mediaPlayer.setLooping(true);
        IntentFilter filter = new IntentFilter(ACTION_STOP_BACKGROUND_MUSIC);
        registerReceiver(stopMusicReceiver, filter);
    }//The broadcastReceiver is registered with intentFilter object that specifies the action it wants to listen for
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {//when we do start service
        mediaPlayer.start();
        return START_STICKY;
    }
    @Override
    public void onDestroy() {//when we want to stop service
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        unregisterReceiver(stopMusicReceiver);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {//overridden to return null because im not need to bind to this service
        return null;
    }
}