package testo.pl.sprawdzacz;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int SECONDS = 60; //every 1 minute!!!
    private static MediaPlayer player;

    public static MediaPlayer getPlayer() {
        return player;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        player = MediaPlayer.create(MainActivity.this, R.raw.tweet);

        setContentView(R.layout.activity_main);
        Pair<Integer, Integer> latestEpisode = new Pair<>(7, 1);
        RecentEpisode.getInstance().insert(this, latestEpisode);
        List<Pair<Integer, Integer>> latestEpisodes = RecentEpisode.getInstance().getRecentEpisodes(this);
        Log.d(TAG, "recent episodes: ");
        for (Pair pair : latestEpisodes) {
            Log.d(TAG, " regular show season " + pair.first + ", episode " + pair.second);
        }
        SoundPlayer.getInstance(this);

        Button x = (Button) findViewById(R.id.latest);
        x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SoundPlayer.getInstance(MainActivity.this).play();

            }
        });


        setupStartAndStopServiceListener();
        Button b = (Button) findViewById(R.id.iview);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView tx = (TextView) findViewById(R.id.tview);
                final TextView titleX = (TextView) findViewById(R.id.title);

                SimpleRestAdapter adapter = new SimpleRestAdapter(WebService.BASE_URL);
                WebService service = adapter.getRestAdapter().create(WebService.class);

                EditText seas = (EditText) findViewById(R.id.season);
                final EditText epis = (EditText) findViewById(R.id.episode);
                final Integer season = Integer.valueOf(seas.getText().toString());
                final Integer episode = Integer.valueOf(epis.getText().toString());

                service.getNews(season.toString(), episode.toString(), new Callback<Response>() {
                    @Override
                    public void success(Response result, Response response) {

                        Log.d(TAG, "success, url = " + response.getUrl());
                        Log.d(TAG, "success = " + response.toString());
                        BufferedReader reader;
                        StringBuilder sb = new StringBuilder();
                        try {
                            reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                            String line;
                            try {
                                while ((line = reader.readLine()) != null) {
                                    sb.append(line);
                                }
                            } catch (IOException e) {
                                Log.d(TAG, "GURWA");
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            Log.d(TAG, "kurde");
                            e.printStackTrace();
                        }

                        String selectedEpisode = "Regular Show Season " + season + " Episode " + episode;
                        String title = "";
                        String _result_ = sb.toString();
                        Log.d(TAG, "received result : " + _result_);
                        if (_result_.contains(selectedEpisode)) {
                            String pretty = _result_.replaceAll("<", "").replaceAll(">", "")
                                    .replaceAll("%7C", "")
                                    .replaceAll("%2C", "|")
                                    .replaceAll("%20", " ");


                            String[] withHtmlCharacters = pretty.split("title");

                            for (String string : withHtmlCharacters) {
                                Log.d(TAG, "new string: " + string);
                                if (string.contains(selectedEpisode)) {
                                    String[] closer = string.split("\\|");
                                    title = closer[0].replaceAll(selectedEpisode, "");
                                    title= title.replaceAll("=","").replaceAll("Watch cartoons online","");
//                                    Log.d(TAG, "title:" + );
                                }
                            }

                            titleX.setText(title);
                            tx.setText(selectedEpisode + " AVAILABLE!!!");
                            buildNotification(season, episode, true);
                            RecentEpisode
                                    .getInstance()
                                    .insert(MainActivity.this, new Pair<>(season, episode));

                        } else {
                            tx.setText(selectedEpisode + " unavailable");
                            buildNotification(season, episode, false);

                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(TAG, "error = " + error.getUrl());
                        Log.d(TAG, "error = " + error.toString());
                        tx.setText("Episode " + episode.toString()
                                + " of season " + season.toString() + " unavailable");
                    }
                });
            }
        });
    }

    private void setupStartAndStopServiceListener() {
        Button start = (Button) findViewById(R.id.startService);
        Button stop = (Button) findViewById(R.id.stopService);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (App.intentService == null)
                    Log.d(TAG, "intent is null, recreating");
                else {
//                    performing stopping existing intent
                    stopService(App.intentService);
                }
                App.intentService = new Intent(MainActivity.this, Notificator.class);

                Calendar cal = Calendar.getInstance();
                PendingIntent pendingIntent = PendingIntent
                        .getService(MainActivity.this, 0, App.intentService, 0);

                AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                // Start service every hour
                alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                        SECONDS * 1000, pendingIntent);

            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (App.intentService != null)
                    stopService(App.intentService);
            }
        });
    }

    private void buildNotification(Integer season, Integer episode, boolean isHere) {
        Log.d(TAG, "show notification");
        String title = (isHere) ? "available!!!" : "unavailable.";
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.mew)
                        .setContentTitle("New Regular Show " + title)
                        .setContentText("Episode " + episode + " of season " + season + " is now " + title);
        final String ringTone = "default ringtone"; // or store in preferences, and fallback to this
        mBuilder.setSound(Uri.parse(ringTone));
// Sets an ID for the notification
        int mNotificationId = 1;
// Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
