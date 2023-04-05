package com.app.SyrianskaTaekwondo.hejtelge.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class MonthlyEventResponse implements Serializable {
    @SerializedName("data")
    @Expose
    public ArrayList<Datum> data = null;
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

    @SerializedName("team_id")
    @Expose
    public String team_id;

    @SerializedName("user_id")
    @Expose
    public String user_id;

    public MonthlyEventResponse(String access_key, String limit, String offset, String team_id, String user_id) {
        this.access_key = access_key;
        this.limit = limit;
        this.offset = offset;
        this.team_id = team_id;
        this.user_id = user_id;
    }

    public static class Datum {

        @SerializedName("order")
        @Expose
        public Integer order;

        @SerializedName("months")
        @Expose
        public String months;
        @SerializedName("monthlyDateVM")
        @Expose
        public ArrayList<MonthlyDateVM> monthlyDateVM = null;
    }

    public static class MonthlyDateVM {

        @SerializedName("date")
        @Expose
        public String date;
        @SerializedName("eventDetails")
        @Expose
        public ArrayList<EventDetail> eventDetails = null;
    }

    public class EventDetail {

        @SerializedName("orderid")
        @Expose
        public Integer orderid;
        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("title")
        @Expose
        public String title;
        @SerializedName("description")
        @Expose
        public String description;
        @SerializedName("place")
        @Expose
        public String place;
        @SerializedName("team_id")
        @Expose
        public String teamId;
        @SerializedName("team_name")
        @Expose
        public String teamName;
        @SerializedName("start_date")
        @Expose
        public String startDate;
        @SerializedName("end_date")
        @Expose
        public String endDate;
        @SerializedName("created_by")
        @Expose
        public String createdBy;
        @SerializedName("updated_by")
        @Expose
        public String updatedBy;
        @SerializedName("status")
        @Expose
        public String status;
        @SerializedName("created_at")
        @Expose
        public String createdAt;
        @SerializedName("is_public")
        @Expose
        public String isPublic;
        @SerializedName("app_id")
        @Expose
        public Object appId;
        @SerializedName("user")
        @Expose
        public String user;
        @SerializedName("attending")
        @Expose
        public String attending;
        @SerializedName("may_be")
        @Expose
        public String mayBe;
        @SerializedName("no_response")
        @Expose
        public String noResponse;
        @SerializedName("denied")
        @Expose
        public String denied;
        @SerializedName("user_id")
        @Expose
        public String userId;
        @SerializedName("insertby")
        @Expose
        public String insertby;
        @SerializedName("details")
        @Expose
        public ArrayList<Object> details = null;
    }

}
