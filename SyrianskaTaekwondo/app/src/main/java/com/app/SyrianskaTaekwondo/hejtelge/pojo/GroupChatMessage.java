package com.app.SyrianskaTaekwondo.hejtelge.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class GroupChatMessage implements Serializable {
   private String groupId;
   private String groupImage;
    private String groupName;
    private String message;
    private String messageId;
    private String senderId;
    private String senderImage;
    private String senderName;
    private String time;
    private String date;
    private String chatImg;
    private ArrayList<HashMap<String,String>> post_images;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderImage() {
        return senderImage;
    }

    public void setSenderImage(String senderImage) {
        this.senderImage = senderImage;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getChatImg() {
        return chatImg;
    }

    public void setChatImg(String chatImg) {
        this.chatImg = chatImg;
    }

    public ArrayList<HashMap<String,String>> getPost_images() {
        return post_images;
    }

    public void setPost_images(ArrayList<HashMap<String,String>> post_images) {
        this.post_images = post_images;
    }


}
