package com.app.SyrianskaTaekwondo.hejtelge.pojo;


import com.app.SyrianskaTaekwondo.hejtelge.model.CollapsibleCalendarEvent;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.List;

public class Event extends CollapsibleCalendarEvent {

    private long mDate;
    private String eventdate;
    private String startdate;
    private String team_name;

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    String enddate;


    public long getmDate() {
        return mDate;
    }

    public void setmDate(long mDate) {
        this.mDate = mDate;
    }

    public String getEventdate() {
        return eventdate;
    }

    public void setEventdate(String eventdate) {
        this.eventdate = eventdate;
    }

    public String getEndinghour() {
        return endinghour;
    }

    public void setEndinghour(String endinghour) {
        this.endinghour = endinghour;
    }

    public String getEndingmin() {
        return endingmin;
    }

    public void setEndingmin(String endingmin) {
        this.endingmin = endingmin;
    }

    public String getEventcolor() {
        return eventcolor;
    }

    public void setEventcolor(String eventcolor) {
        this.eventcolor = eventcolor;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartinghour() {
        return startinghour;
    }

    public void setStartinghour(String startinghour) {
        this.startinghour = startinghour;
    }

    public String getStartingmin() {
        return startingmin;
    }

    public void setStartingmin(String startingmin) {
        this.startingmin = startingmin;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIsEvent() {
        return isEvent;
    }

    public void setIsEvent(String isEvent) {
        this.isEvent = isEvent;
    }

    public String getUserstatus() {
        return userstatus;
    }

    public void setUserstatus(String userstatus) {
        this.userstatus = userstatus;
    }

    public String getInsertby() {
        return insertby;
    }

    public void setInsertby(String insertby) {
        this.insertby = insertby;
    }


    public String getTeam_id() {
        return team_id;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    private String team_id;
    private String endinghour;
    private String endingmin;
    private String eventcolor;
    private String title;
    private String detail;
    private String id;
    private String startinghour;
    private String startingmin;
    private String location;
    private String isEvent;
    private String userstatus;
    private String insertby;

    public List<Details> getDetails() {
        return details;
    }

    public void setDetails(List<Details> details) {
        this.details = details;
    }

    private List<Details> details;

    public String getAttending() {
        return attending;
    }

    public void setAttending(String attending) {
        this.attending = attending;
    }

    String attending;

    public String getMay_be() {
        return may_be;
    }

    public void setMay_be(String may_be) {
        this.may_be = may_be;
    }

    public String getNo_response() {
        return no_response;
    }

    public void setNo_response(String no_response) {
        this.no_response = no_response;
    }

    public String getDenied() {
        return denied;
    }

    public void setDenied(String denied) {
        this.denied = denied;
    }

    String may_be;
    String no_response;
    String denied;

    public Event(String title, long date, String endinghour, String endingmin, String eventcolor, String detail, String id, String startinghour, String startingmin, String location, String isEvent, String userstatus, String insertby, String Attending, String NotAnswer, String NotAtending, String Maybe, String eventdate, List<Details> details) {
        mDate = date;
        this.endinghour = endinghour;
        this.endingmin = endingmin;
        this.eventcolor = eventcolor;
        this.detail = detail;
        this.id = id;
        this.startinghour = startinghour;
        this.startingmin = startingmin;
        this.location = location;
        this.isEvent = isEvent;
        this.userstatus = userstatus;
        this.insertby = insertby;
        this.attending = Attending;
        this.no_response = NotAnswer;
        this.denied = NotAtending;
        this.may_be = Maybe;
        this.eventdate = eventdate;
        this.details = details;
    }

    public Event() {
    }

    public String getTitle() {

        return title;
    }

    public DateTime getListCellTime() {
        return new DateTime(mDate);
    }

    @Override
    public LocalDate getCollapsibleEventLocalDate() {

        return new LocalDate(mDate);
    }

    public String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }
}