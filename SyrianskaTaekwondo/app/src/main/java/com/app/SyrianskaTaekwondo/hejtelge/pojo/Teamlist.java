package com.app.SyrianskaTaekwondo.hejtelge.pojo;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import vk.help.Common;

public class Teamlist implements Serializable {
   public String getId() {
       return id;
   }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getCoach_id() {
        return coach_id;
    }

    public void setCoach_id(String coach_id) {
        this.coach_id = coach_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<UserList> getUsers() {
        return users;
    }

    public void setUsers(List<UserList> users) {
        this.users = users;
    }

    private String id = "";
    private String chatstarted = "";
    private String name = "";

    public String getRole_id() {
        return role_id;
    }

    public void setRole_id(String role_id) {
        this.role_id = role_id;
    }

    private String role_id = "";
    private String logo = "";
    private String coach_id = "";
    private String status = "";

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String username = "";
    private List<UserList> users = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Teamlist teamlist = (Teamlist) o;
        return Objects.equals(id, teamlist.id) &&
                Objects.equals(name, teamlist.name) &&
                Objects.equals(role_id, teamlist.role_id) &&
                Objects.equals(logo, teamlist.logo) &&
                Objects.equals(coach_id, teamlist.coach_id) &&
                Objects.equals(status, teamlist.status) &&
                Objects.equals(users, teamlist.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, role_id, logo, coach_id, status, users);
    }

    @NotNull
    @Override
    public String toString() {
        return Common.INSTANCE.getJSON(this);
    }

    public String getChatstarted() {
        return chatstarted;
    }

    public void setChatstarted(String chatstarted) {
        this.chatstarted = chatstarted;
    }
}
