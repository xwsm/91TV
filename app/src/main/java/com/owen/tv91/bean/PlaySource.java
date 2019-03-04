package com.owen.tv91.bean;

import java.util.List;

public class PlaySource {

    public long id;
    public long movieId;
    public String name;
    public String updateDate;

    public List<PlayUrl> playUrls;

    public boolean hasPlayUrls() {
        return null != playUrls && !playUrls.isEmpty();
    }

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
