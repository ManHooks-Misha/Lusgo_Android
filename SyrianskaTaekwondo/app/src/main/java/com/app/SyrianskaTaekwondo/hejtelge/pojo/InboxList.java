package com.app.SyrianskaTaekwondo.hejtelge.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InboxList implements Serializable {

    private String message_id = "";
    private String message = "";
    private String senderuserid = "";
    private String teamName = "";


    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    private String isRead = "";
    private String link = "";
    private String document = "";
    private String name = "";
    private String profile_image = "";
    private String sender_id = "";

    public String getReadCount() {
        return readCount;
    }

    public void setReadCount(String readCount) {
        this.readCount = readCount;
    }

    private String readCount = "";


    public List<IsReadUserArr> getRead_users() {
        return read_users;
    }

    public void setRead_users(List<IsReadUserArr> read_users) {
        this.read_users = read_users;
    }

    private List<IsReadUserArr> read_users;

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    private String created = "";

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    private String role = "";

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderuserid() {
        return senderuserid;
    }

    public void setSenderuserid(String senderuserid) {
        this.senderuserid = senderuserid;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public ArrayList<HashMap<String, String>> getImages() {
        return images;
    }

    public void setImages(ArrayList<HashMap<String, String>> images) {
        this.images = images;
    }

    private ArrayList<HashMap<String, String>> images = new ArrayList<>();

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}
