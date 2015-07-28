package testo.pl.sprawdzacz;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.lang.reflect.Type;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by Lukasz Marczak on 2015-07-28.
 */
public class SimpleRestAdapter {
    private RestAdapter restAdapter;

    public SimpleRestAdapter(String endpoint) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .setConverter(new GsonConverter(gson))
                .build();
    }

    /**
     * @param endpoint - base url
     * @param collectionType - type of collection to return, for example List<LatLng>
     * @param deserializer - class which deserializes your json responses
     */
    public SimpleRestAdapter(String endpoint, Type collectionType, JsonDeserializer deserializer) {
        GsonBuilder gsonBuilder = new GsonBuilder();


        gsonBuilder.registerTypeAdapter(collectionType, deserializer);
        Gson gson = gsonBuilder.create();

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .setConverter(new GsonConverter(gson))
                .build();
    }

    public RestAdapter getRestAdapter() {
        return restAdapter;
    }
}