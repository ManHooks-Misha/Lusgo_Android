package com.app.SyrianskaTaekwondo.hejtelge.pojo;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import vk.help.Common;

public class UserList implements Serializable, Comparable<UserList> {
    private String id = "";
    private String firstname = "";
    private String lastname = "";
    private String role = "";
    private String role_name = "";
    private String phone = "";
    private String email = "";
    private String imagepath = "";
    private String banner = "";

    public String getRole_id() {
        return role_id;
    }

    public void setRole_id(String role_id) {
        this.role_id = role_id;
    }

    private String role_id = "";

    public String getCoach_id() {
        return coach_id;
    }

    public void setCoach_id(String coach_id) {
        this.coach_id = coach_id;
    }

    private String coach_id = "";
    private String profile_image = "";

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status = "";
    private String banner_image = "";

    public List<Team_search> getTeams() {
        return teams;
    }

    public void setTeams(List<Team_search> teams) {
        this.teams = teams;
    }

    private List<Team_search> teams=new ArrayList<>();

    public String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    private String team_name = "";

    private Permissions permissions;

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    private String created = "";

    public String getTeam_id() {
        return team_id;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    private String team_id = "";

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String username = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getBanner_image() {
        return banner_image;
    }

    public void setBanner_image(String banner_image) {
        this.banner_image = banner_image;
    }

    public Permissions getPermissions() {
        return permissions;
    }

    public void setPermissions(Permissions permissions) {
        this.permissions = permissions;
    }


    @Override
    public int compareTo(@NotNull UserList userList) {
        int last = this.getFirstname().compareTo(userList.getFirstname());
        return last == 0 ? this.getLastname().compareTo(userList.getLastname()) : last;
    }

    @NonNull
    @Override
    public String toString() {
        return Common.INSTANCE.getJSON(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserList userList = (UserList) o;
        return Objects.equals(id, userList.id) &&
                Objects.equals(firstname, userList.firstname) &&
                Objects.equals(lastname, userList.lastname) &&
                Objects.equals(role, userList.role) &&
                Objects.equals(phone, userList.phone) &&
                Objects.equals(email, userList.email) &&
                Objects.equals(imagepath, userList.imagepath) &&
                Objects.equals(banner, userList.banner) &&
                Objects.equals(profile_image, userList.profile_image) &&
                Objects.equals(banner_image, userList.banner_image) &&
                Objects.equals(permissions, userList.permissions) &&
                Objects.equals(created, userList.created) &&
                Objects.equals(team_id, userList.team_id) &&
                Objects.equals(team_name,userList.team_name) &&
                Objects.equals(username, userList.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstname, lastname, role, phone, email, imagepath, banner, profile_image, banner_image, permissions, created, team_id, username,team_name);
    }

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }
}