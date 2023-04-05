package com.app.SyrianskaTaekwondo.hejtelge.utils;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by sab99r
 */
public interface NewsApi {
    @GET("newsListGet")
    Call<JSONObject> getNews(@Query("access_key") String access_key, @Query("user_id") String user_id, @Query("offset") String offset, @Query("limit") String limit, @Query("team_id") String team_id);
}
