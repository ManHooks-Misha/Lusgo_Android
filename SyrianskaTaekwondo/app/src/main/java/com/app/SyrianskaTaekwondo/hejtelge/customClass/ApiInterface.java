package com.app.SyrianskaTaekwondo.hejtelge.customClass;

import com.app.SyrianskaTaekwondo.hejtelge.pojo.MonthlyEventResponse;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.ReadAllNotificationModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("MonthlyeventList")
    Call<MonthlyEventResponse> getMonthlyEvents(@Body MonthlyEventResponse monthlyEventResponse);

    @POST("read_Allnotification")
    Call<ReadAllNotificationModel> readAllNotification(@Body ReadAllNotificationModel readAllNotificationModel);


}
