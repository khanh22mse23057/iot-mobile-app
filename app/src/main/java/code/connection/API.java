package code.connection;

import java.util.Map;

import code.connection.response.RespNews;
import code.model.JsonDataFeedModel;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface API {

    String CACHE = "Cache-Control: max-age=0";
    String AGENT = "User-Agent: MaterialX";

    String API_URL = "https://io.adafruit.com/api/v2/";

    @Headers({CACHE, AGENT})
    @GET("api/news")
    Call<RespNews> getNews(@QueryMap Map<String, String> params);

    @GET("{username}{feed_key}")
    Call<ResponseBody> getFeed(
            @Path("username") String username,
            @Path(value = "feed_key", encoded = true) String feedKey, @Query("x-aio-key") String token
    );

}
