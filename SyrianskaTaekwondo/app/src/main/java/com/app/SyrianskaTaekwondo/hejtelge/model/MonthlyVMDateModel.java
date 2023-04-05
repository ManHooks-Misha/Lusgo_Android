package com.app.SyrianskaTaekwondo.hejtelge.model;

import com.app.SyrianskaTaekwondo.hejtelge.pojo.MonthlyEventResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MonthlyVMDateModel {

    @SerializedName("date")
    @Expose
    public String date;
    @SerializedName("eventDetails")
    @Expose
    public ArrayList<MonthlyEventResponse.EventDetail> eventDetails = null;

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
