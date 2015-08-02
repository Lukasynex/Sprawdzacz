package testo.pl.sprawdzacz;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.util.Pair;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Lukasz Marczak on 2015-08-02.
 */
public class RecentEpisode {
    public static final String TAG = RecentEpisode.class.getSimpleName();
    public static final String RECENT = "RECENT";
    public static boolean mutex = false;
    public static RecentEpisode instance = null;

    public static RecentEpisode getInstance() {
        if (instance == null)
            instance = new RecentEpisode();
        return instance;
    }

    private RecentEpisode() {
    }

    public void insert(Context context, Pair<Integer, Integer> mostRecent) {
        if (mutex)
            return;
        Log.d(TAG, "inserting new episode...");
        SharedPreferences.Editor editor = context.getSharedPreferences(RECENT, Context.MODE_PRIVATE).edit();
        List<Pair<Integer, Integer>> currentList = getRecentEpisodes(context);
        if (currentList == null) {
            Log.d(TAG, "new recent");

            currentList = new ArrayList<>();
            currentList.add(mostRecent);
            Log.d(TAG, "success!!!");

        } else {
            boolean isAlreadyInRecent = false;
            for (Pair bl : currentList) {
                if (bl.first == mostRecent.first && bl.second == mostRecent.second) {
                    isAlreadyInRecent = true;
                }
            }
            if (!isAlreadyInRecent) {
                currentList.add(mostRecent);
                Log.d(TAG, "success!!!");
            } else {
                Log.d(TAG, "failed ");
            }
        }
        Collections.sort(currentList, new Comparator<Pair<Integer, Integer>>() {
            @Override
            public int compare(Pair<Integer, Integer> episodeA, Pair<Integer, Integer> episodeB) {
                if (episodeA.first < episodeB.first) {
                    return 1;
                } else if (episodeA.first > episodeB.first) {
                    return -1;
                } else if (episodeA.second < episodeB.second) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });

        // convert java object to JSON format,
        // and returned as JSON formatted string
        String recentJsonString = new Gson().toJson(currentList);
        editor.putString(RECENT, recentJsonString);
        editor.commit();
        mutex = false;
    }

    public List<Pair<Integer, Integer>> getRecentEpisodes(Context context) {

        String episodeAsJson = context.getSharedPreferences(RECENT, Context.MODE_PRIVATE).getString(RECENT, null);
        Type type = new TypeToken<List<Pair<Integer, Integer>>>() {
        }.getType();
        if (episodeAsJson == null)
            return new ArrayList<>();
        else return new Gson().fromJson(episodeAsJson, type);
    }
}
