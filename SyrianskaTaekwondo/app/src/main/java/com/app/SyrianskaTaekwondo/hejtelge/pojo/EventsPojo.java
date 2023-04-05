package com.app.SyrianskaTaekwondo.hejtelge.pojo;

import java.util.ArrayList;
import java.util.HashMap;

public class EventsPojo {


    public String access_key;
    public String title;
    public String description;
    public String start_date;
    public String end_date;
    public String place;
    public String is_public;
    public String user_id;
    public String team_id;
    public String coach_id;
    public ArrayList<String> users=new ArrayList<>();
    public ArrayList<String> groups=new ArrayList<>();
    public ArrayList<HashMap<String,String>> Team_users=new ArrayList<>();

}
