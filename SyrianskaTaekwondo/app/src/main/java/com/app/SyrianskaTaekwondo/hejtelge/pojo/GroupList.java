package com.app.SyrianskaTaekwondo.hejtelge.pojo;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class GroupList implements Serializable {

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getInsertdate() {
        return insertdate;
    }

    public void setInsertdate(String insertdate) {
        this.insertdate = insertdate;
    }

    public String getInsertby() {
        return insertby;
    }

    public void setInsertby(String insertby) {
        this.insertby = insertby;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    private String group_id = "";
    private String image = "";
    private String insertdate = "";
    private String insertby = "";
    private String name = "";
    private String status = "";
    private String chat_for = "";

    public String getChatstarted() {
        return chatstarted;
    }

    public void setChatstarted(String chatstarted) {
        this.chatstarted = chatstarted;
    }

    private String chatstarted="";
    private List<UserList> users;

    @Override
    public String toString() {
        return "GroupList{" +
                "group_id='" + group_id + '\'' +
                ", image='" + image + '\'' +
                ", insertdate='" + insertdate + '\'' +
                ", insertby='" + insertby + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", users=" + users +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupList groupList = (GroupList) o;
        return Objects.equals(group_id, groupList.group_id) &&
                Objects.equals(image, groupList.image) &&
                Objects.equals(insertdate, groupList.insertdate) &&
                Objects.equals(insertby, groupList.insertby) &&
                Objects.equals(name, groupList.name) &&
                Objects.equals(status, groupList.status) &&
                Objects.equals(users, groupList.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group_id, image, insertdate, insertby, name, status, users);
    }

    public String getChat_for() {
        return chat_for;
    }

    public void setChat_for(String chat_for) {
        this.chat_for = chat_for;
    }
}
