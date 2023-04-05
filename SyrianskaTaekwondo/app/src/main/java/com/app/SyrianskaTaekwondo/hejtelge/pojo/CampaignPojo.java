package com.app.SyrianskaTaekwondo.hejtelge.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class CampaignPojo implements Serializable {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<HashMap<String, String>> getSponsers() {
        return sponsers;
    }

    public void setSponsers(ArrayList<HashMap<String, String>> sponsers) {
        this.sponsers = sponsers;
    }

    private String id;
    private String start_date;
    private String end_date;
    private String app_id;
    private String created_by;
    private String created_at;
    private String status;
    private ArrayList<HashMap<String, String>> sponsers;

}
