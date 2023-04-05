package com.app.SyrianskaTaekwondo.hejtelge.pojo;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

import vk.help.Common;

public class IsReadUserArr implements Serializable {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        isRead = isRead;
    }

    private String username;
    private String last_name;
    private String profile_image;
    private String first_name;
    private String isRead;

    @NotNull
    public String toString() {
        return Common.INSTANCE.getJSON(this);
    }
}
