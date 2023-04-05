package com.app.SyrianskaTaekwondo.hejtelge.customClass;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created on : Feb 25, 2019
 * Author     : AndroidWave
 */
public interface RestApiService {


    @Multipart
    @POST("GroupChattingNew")
    Single<ResponseBody> onFileUpload(@Part("userid") RequestBody userid,@Part("groupid") RequestBody groupid,@Part("msg") RequestBody msg,@Part("chat_for") RequestBody chat_for, @Part MultipartBody.Part file);

}
