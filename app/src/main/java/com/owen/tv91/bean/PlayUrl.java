package com.owen.tv91.bean;


public class PlayUrl {

    public long id;
    public String name;
    public String playUrl;
    public long playSourceId;
    public String updateDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public long getPlaySourceId() {
        return playSourceId;
    }

    public void setPlaySourceId(long playSourceId) {
        this.playSourceId = playSourceId;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
