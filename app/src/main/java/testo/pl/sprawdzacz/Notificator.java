package testo.pl.sprawdzacz;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class Notificator extends Service {

    public static final String TAG = Notificator.class.getSimpleName();
    final Uri soundUri;

    public Notificator() {
        soundUri = Uri.parse("android.resource://testo.pl.sprawdzacz/raw/tweet");
    }

    @Override
    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, " MyService Created ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Toast.makeText(this, " MyService Started", Toast.LENGTH_LONG).show();
        List<Pair<Integer, Integer>> recentEpisodes = RecentEpisode.getInstance().getRecentEpisodes(this);
        Log.d(TAG, "latest episodes:");
        for (Pair pe : recentEpisodes) {
            Log.d(TAG, "season: " + pe.first + ", episode: " + pe.second);
        }
        Pair<Integer, Integer> mostRecent = recentEpisodes.get(0);
        buildNotification(mostRecent.first, mostRecent.second, true);
//        if (mPlayer != null && !mPlayer.isPlaying()) {
//            mPlayer.start();
//        }
//        SoundPlayer.getInstance(this).play();

    }

    private void buildNotification(Integer season, Integer episode, boolean isHere) {
        Log.d(TAG, "show notification");
        String title = (isHere) ? "available!!!" : "unavailable.";
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.mew)
                        .setContentTitle("New Regular Show " + title)
                        .setContentText("Episode " + episode + " of season " + season + " is now " + title)
                        .setSound(soundUri);

        int mNotificationId = 1;
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        Toast.makeText(this, "Servics Stopped", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
}
