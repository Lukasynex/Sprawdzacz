package testo.pl.sprawdzacz;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Lukasz Marczak on 2015-07-28.
 */
public class Deserializer implements JsonDeserializer<String> {
    public static final String TAG =  Deserializer.class.getSimpleName();
    public Deserializer() {  }
    @Override
    public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Log.d(TAG, "received json = "+json.toString());
        final JsonObject jsonObject = json.getAsJsonObject();

        final JsonArray array = jsonObject.get("results").getAsJsonArray();
        String out = "null";
        return out;
    }
}

