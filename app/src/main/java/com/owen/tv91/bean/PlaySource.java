package com.owen.tv91.bean;

import java.util.List;

public class PlaySource {

    public long id;
    public String name;
    public String type;

    public List<PlayUrl> playUrls;

    public boolean hasPlayUrls() {
        return null != playUrls && !playUrls.isEmpty();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<PlayUrl> getPlayUrls() {
        return playUrls;
    }

    public void setPlayUrls(List<PlayUrl> playUrls) {
        this.playUrls = playUrls;
    }
}
