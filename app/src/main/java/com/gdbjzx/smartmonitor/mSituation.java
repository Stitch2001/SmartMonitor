package com.gdbjzx.smartmonitor;

/**
 * Created by Administrator on 2018/8/17.
 */

public class mSituation {

    private String location;

    private String event;

    private int score;

    public mSituation(String location,String event,int score){
        this.location = location;
        this.event = event;
        this.score = score;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

}
