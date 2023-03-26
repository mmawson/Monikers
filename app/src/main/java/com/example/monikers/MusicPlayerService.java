package com.example.monikers;

import android.app.IntentService;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

public class MusicPlayerService extends IntentService {
    public MusicPlayerService() {
        super("MusicPlayerService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d("Monikers", "onHandleIntent called");
        MediaPlayer mPlayer = MediaPlayer.create(this, R.raw.background_music);

        int maxVolume = 100;
        int currVolume = 99;

        float volume = (float) (1 - (Math.log(maxVolume - currVolume) / Math.log(maxVolume)));
        mPlayer.setVolume(currVolume, currVolume);
        mPlayer.setLooping(true);
        mPlayer.start();

        while (true)
        {
            //Keep this thread alive indefinitely, so the music keeps playing
        }
    }
}
