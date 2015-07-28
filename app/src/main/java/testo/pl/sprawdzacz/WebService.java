package testo.pl.sprawdzacz;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Lukasz Marczak on 2015-07-28.
 */
public interface WebService {
    String BASE_URL = "http://www.watchcartoononline.com/";

    @GET("/regular-show-season-{seaso}-episode-{episod}")
    void getNews(@Path("seaso") String season,
                 @Path("episod") String episode,
                 Callback<Response> jsonElementCallback);
}
