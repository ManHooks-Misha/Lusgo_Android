package com.app.SyrianskaTaekwondo.hejtelge.customClass;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {
    public static final String BASE_URL = "https://web.lusgo.se/api/TKW/";

    private static Retrofit retrofit = null;
    public static Retrofit getClient() {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.connectTimeout(1, TimeUnit.HOURS);
        client.readTimeout(1,TimeUnit.HOURS);
        client.writeTimeout(1,TimeUnit.HOURS);
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client.build())
                    .build();
        }
        return retrofit;
    }
}
