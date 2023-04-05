package com.app.SyrianskaTaekwondo.hejtelge.pojo;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

import vk.help.Common;

public class CoachModel implements Serializable, Comparable<CoachModel> {

    private String id = "";
    private String firstname = "";
    private String lastname = "";
    private String role = "";
    private String phone = "";
    private String email = "";
    private String imagepath = "";
    private String banner = "";
    private String username = "";
    private String team_id = "";
    private String profile_image = "";
    private String banner_image = "";

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTeam_id() {
        return team_id;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
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

    @NotNull
    @Override
    public String toString() {
        return Common.INSTANCE.getJSON(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoachModel that = (CoachModel) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(firstname, that.firstname) &&
                Objects.equals(lastname, that.lastname) &&
                Objects.equals(role, that.role) &&
                Objects.equals(phone, that.phone) &&
                Objects.equals(email, that.email) &&
                Objects.equals(imagepath, that.imagepath) &&
                Objects.equals(banner, that.banner) &&
                Objects.equals(username, that.username) &&
                Objects.equals(team_id, that.team_id) &&
                Objects.equals(profile_image, that.profile_image) &&
                Objects.equals(banner_image, that.banner_image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstname, lastname, role, phone, email, imagepath, banner, username, team_id, profile_image, banner_image);
    }

    @Override
    public int compareTo(@NotNull CoachModel coachModel) {
        return 0;
    }
}
