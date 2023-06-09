package com.app.SyrianskaTaekwondo.hejtelge.customClass;

import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created on : Feb 25, 2019
 * Author     : AndroidWave
 */
public class RetrofitInstance {

    private static Retrofit retrofit = null;

    public static RestApiService getApiService() {

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES).addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder requestBuilder = requestBuilder = original.newBuilder()
                            .method(original.method(), original.body());
                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }).build();

        if (retrofit == null) {

            retrofit = new Retrofit
                    .Builder()
                    .baseUrl(ConsURL.BASE_URL_TEST2)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

        }

        return retrofit.create(RestApiService.class);

    }

}
