package code.connection;

import android.content.Context;

import code.connection.response.RespNews;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Request {

    private Context context;
    private API api = RestAdapter.createAPI();

    public Request(Context context) {
        this.context = context;
    }

    public Call<RespNews> getNews(ParamList param, RequestListener<RespNews> listener) {
        Call<RespNews> callbackCall = api.getNews(param.getParams());
        callbackCall.enqueue(new Callback<RespNews>() {
            @Override
            public void onResponse(Call<RespNews> call, Response<RespNews> response) {
                RespNews resp = response.body();
                listener.onFinish();
                if (resp == null) {
                    listener.onFailed(null);
                } else if(resp.status != 200) {
                    listener.onFailed(resp.messages);
                } else{
                    listener.onSuccess(resp);
                }
            }

            @Override
            public void onFailure(Call<RespNews> call, Throwable t) {
                listener.onFinish();
                listener.onFailed(t.getMessage());
            }
        });
        return callbackCall;
    }


}
