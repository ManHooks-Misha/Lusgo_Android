package com.app.SyrianskaTaekwondo.hejtelge.pojo;

import java.util.ArrayList;
import java.util.HashMap;

public class Messege {

    public String access_key;
    public String message;
    public String is_public;
    public String user_id;
    public String link;
    public String team_id;
    public String doc;
    public ArrayList<String> groups;
    public ArrayList<String> users;
    public ArrayList<HashMap<String, String>> images;
    public ArrayList<HashMap<String, String>> Team_users;
    public boolean all_coaches = false;
    public boolean all_users = false;
    public boolean all_groups = false;
    public boolean all_teams = false;
}
