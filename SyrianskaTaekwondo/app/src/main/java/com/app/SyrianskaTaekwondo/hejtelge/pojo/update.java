package com.app.SyrianskaTaekwondo.hejtelge.pojo;

import java.util.HashMap;
import java.util.List;

public class update{

    public List<HashMap<String, String>> getLink() {
        return link;
    }

    public void setLink(List<HashMap<String, String>> link) {
        this.link = link;
    }

    public List<HashMap<String, String>> getContactlist() {
        return contactlist;
    }

    public void setContactlist(List<HashMap<String, String>> contactlist) {
        this.contactlist = contactlist;
    }

    public List<HashMap<String, String>> getDocument() {
        return document;
    }

    public void setDocument(List<HashMap<String, String>> document) {
        this.document = document;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAccess_key() {
        return access_key;
    }

    public void setAccess_key(String access_key) {
        this.access_key = access_key;
    }

  public   List<HashMap<String, String>> link;
    public  List<HashMap<String,String>> contactlist;
    public List<HashMap<String, String>> document;
    public String user_id;
    public String access_key;
    public  List<String> about_us;
}
