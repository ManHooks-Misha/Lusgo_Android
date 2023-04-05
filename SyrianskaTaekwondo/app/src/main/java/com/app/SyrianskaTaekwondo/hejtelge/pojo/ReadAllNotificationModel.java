package com.app.SyrianskaTaekwondo.hejtelge.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ReadAllNotificationModel implements Serializable {
    @SerializedName("data")
    @Expose
    public ArrayList<ReadAllNotificationModel.Datum> data = null;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("access_key")
    @Expose
    public String access_key;

    @SerializedName("limit")
    @Expose
    public String limit;

    @SerializedName("offset")
    @Expose
    public String offset;

    @SerializedName("user_id")
    @Expose
    public String user_id;

    public ReadAllNotificationModel(String access_key, String limit, String offset, String user_id) {
        this.access_key = access_key;
        this.limit = limit;
        this.offset = offset;
        this.user_id = user_id;
    }
    public static class Datum {

        @SerializedName("seen")
        @Expose
        public String seen;

    }

}
