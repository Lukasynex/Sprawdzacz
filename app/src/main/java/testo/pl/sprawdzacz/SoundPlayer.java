package testo.pl.sprawdzacz;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * Created by Lukasz Marczak on 2015-08-02.
 */
public class SoundPlayer {
    private static SoundPlayer ourInstance = null;
    private MediaPlayer player;
    private static final String TAG = SoundPlayer.class.getSimpleName();

    public static SoundPlayer getInstance(Activity context) {
        if (ourInstance == null)
            ourInstance = new SoundPlayer(context);
        return ourInstance;
    }

    private SoundPlayer(Activity context) {
        player = MediaPlayer.create(context, R.raw.tweet);
        try{
            player.prepare();
        }catch(Exception ally){
            ally.printStackTrace();
            Log.e(TAG, "failed playing");
            player = null;
        }
    }

    public  void play() {
        if (ourInstance != null && player != null && !player.isPlaying()) {
            Log.d(TAG, "playing()");
            player.start();
        } else {
            Log.d(TAG, "Cannot play sound");

        }

    }
}
