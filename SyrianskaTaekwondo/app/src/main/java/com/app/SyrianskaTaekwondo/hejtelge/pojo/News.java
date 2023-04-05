package com.app.SyrianskaTaekwondo.hejtelge.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class News implements Serializable {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
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

    public String getIs_public() {
        return is_public;
    }

    public void setIs_public(String is_public) {
        this.is_public = is_public;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public ArrayList<HashMap<String, String>> getImages() {
        return images;
    }

    public void setImages(ArrayList<HashMap<String, String>> images) {
        this.images = images;
    }


    public News(ArrayList<HashMap<String, String>> images) {
        this.images = images;

    }

    public News(String id, String name, String description, String doc, String profile_image,
                String is_public, String link, ArrayList<HashMap<String, String>> images, String created, String comments, String insertby, String team_name, String type,ArrayList<HashMap<String, String>> images_thumbnail) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.doc = doc;
        this.profile_image = profile_image;
        this.is_public = is_public;
        this.link = link;
        this.images = images;
        this.created = created;
        this.comments = comments;
        this.insertby = insertby;
        this.team_name = team_name;
        this.type = type;
        this.images_thumbnail = images_thumbnail;
    }

    public News(String type) {
        this.type = type;
    }

    private String id;

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    private String comments;


    public ArrayList<HashMap<String, String>> getImages_thumbnail() {
        return images_thumbnail;
    }

    public void setImages_thumbnail(ArrayList<HashMap<String, String>> images_thumbnail) {
        this.images_thumbnail = images_thumbnail;
    }

    private ArrayList<HashMap<String, String>> images_thumbnail = new ArrayList<>();
    private String insertby;
    private String description;
    private String doc;
    private String name;
    private String profile_image;
    private String is_public;
    private String link;
    private String team_name;
    private String youTubeTitle;

    public String getYouTubeTitle() {
        return youTubeTitle;
    }

    public void setYouTubeTitle(String youTubeTitle) {
        this.youTubeTitle = youTubeTitle;
    }

    public String getYouTubeThubnails() {
        return youTubeThubnails;
    }

    public void setYouTubeThubnails(String youTubeThubnails) {
        this.youTubeThubnails = youTubeThubnails;
    }

    private String youTubeThubnails;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type;

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    private String usertype;

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    private String created;
    private ArrayList<HashMap<String, String>> images = new ArrayList<>();


    public String getInsertby() {
        return insertby;
    }

    public void setInsertby(String insertby) {
        this.insertby = insertby;
    }

    public String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }
}
