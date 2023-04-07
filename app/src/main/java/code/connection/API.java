package code.connection;

import java.util.Map;

import code.connection.response.RespNews;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.QueryMap;

public interface API {

    String CACHE = "Cache-Control: max-age=0";
    String AGENT = "User-Agent: MaterialX";

    String API_URL = "https://dream-space.web.id/";

    @Headers({CACHE, AGENT})
    @GET("api/news")
    Call<RespNews> getNews(@QueryMap Map<String, String> params);

}
