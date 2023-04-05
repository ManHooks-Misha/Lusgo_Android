package com.app.SyrianskaTaekwondo.hejtelge.pojo;

import java.io.Serializable;

public class Permissions implements Serializable {
    private String news;
    private String message;
    private String event;

    public String getNews() {
        return news;
    }

    public void setNews(String news) {
        this.news = news;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
