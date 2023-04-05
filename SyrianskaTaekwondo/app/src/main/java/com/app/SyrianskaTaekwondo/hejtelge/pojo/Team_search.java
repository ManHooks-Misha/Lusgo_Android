package com.app.SyrianskaTaekwondo.hejtelge.pojo;

import java.io.Serializable;

public class Team_search implements Serializable {
    public String name;
    public String team_id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeam_id() {
        return team_id;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    public String getCoach_id() {
        return coach_id;
    }

    public void setCoach_id(String coach_id) {
        this.coach_id = coach_id;
    }

    public String coach_id;

}
