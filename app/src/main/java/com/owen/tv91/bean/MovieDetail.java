package com.owen.tv91.bean;


import java.io.Serializable;
import java.util.List;

public class MovieDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    public long id;
    public String channel; //所属频道
    public String img;
    public String name; //姓名，无双
    public String sketch; //简述，HD1280高清国语中字版/更新至20190114
    public String score; //评分，7.9
    public String alias; //英文名/别名，Project Gutenberg,Mo seung
    public String director; //导演，庄文强
    public String starring; //主演，周润发,郭富城,张静初,冯文娟
    public String type; //类型，动作片
    public String types; //动作 爱情
    public String area; //地区，大陆
    public String language; //语言，国语
    public String showDate; //上映日期，2018
    public String duration; //片长，130
    public String updateDate; //更新日期，2018-12-21
    public String intro; //简介

    public List<PlaySource> playSources;

    public boolean hasPlaySources() {
        return null != playSources && !playSources.isEmpty();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSketch() {
        return sketch;
    }

    public void setSketch(String sketch) {
        this.sketch = sketch;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getStarring() {
        return starring;
    }

    public void setStarring(String starring) {
        this.starring = starring;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getShowDate() {
        return showDate;
    }

    public void setShowDate(String showDate) {
        this.showDate = showDate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }
}
