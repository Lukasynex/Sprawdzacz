package testo.pl.sprawdzacz;

import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button b = (Button) findViewById(R.id.iview);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView tx = (TextView) findViewById(R.id.tview);

                SimpleRestAdapter adapter = new SimpleRestAdapter(WebService.BASE_URL);
                WebService service = adapter.getRestAdapter().create(WebService.class);

                EditText seas = (EditText) findViewById(R.id.season);
                final EditText epis = (EditText) findViewById(R.id.episode);
                final String season = seas.getText().toString();
                final String episode = epis.getText().toString();

                service.getNews(season, episode, new Callback<Response>() {
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
                                Log.d(TAG, "kurwa maæ");
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            Log.d(TAG, "kurde");
                            e.printStackTrace();
                        }

                        String _result_ = sb.toString();
                        Log.d(TAG, "received result : " + _result_);
                        if (_result_.contains("Regular Show Season " + season + " Episode " + episode))
                            tx.setText("Episode " + episode + " of season " + season + " AVAILABLE!!!");
                        else
                            tx.setText("Episode " + episode + " of season " + season + " unavailable");
                    }


                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(TAG, "error = " + error.getUrl());
                        Log.d(TAG, "error = " + error.toString());
                        tx.setText("Episode " + episode + " of season " + season + " unavailable");
                    }
                });
            }
        });
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
