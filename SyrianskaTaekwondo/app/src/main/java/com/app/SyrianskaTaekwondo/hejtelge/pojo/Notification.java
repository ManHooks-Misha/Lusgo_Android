package com.app.SyrianskaTaekwondo.hejtelge.pojo;

import java.io.Serializable;

public class Notification implements Serializable {

    public String getN_id() {
        return n_id;
    }

    public void setN_id(String n_id) {
        this.n_id = n_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    private  String n_id;

    public String getSn_id() {
        return sn_id;
    }

    public void setSn_id(String sn_id) {
        this.sn_id = sn_id;
    }

    private  String sn_id;
    private  String message;
    private  String sender;
    private  String created;
    private  String name;
    private  String profile_image;
    private  String role;

    public String getAccepted() {
        return accepted;
    }

    public void setAccepted(String accepted) {
        this.accepted = accepted;
    }

    private  String accepted;

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        isRead = isRead;
    }

    private  String isRead;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private  String type;
}
