package com.owen.tv91.bean;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class MovieDetail implements Parcelable {

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.channel);
        dest.writeString(this.img);
        dest.writeString(this.name);
        dest.writeString(this.sketch);
        dest.writeString(this.score);
        dest.writeString(this.alias);
        dest.writeString(this.director);
        dest.writeString(this.starring);
        dest.writeString(this.type);
        dest.writeString(this.types);
        dest.writeString(this.area);
        dest.writeString(this.language);
        dest.writeString(this.showDate);
        dest.writeString(this.duration);
        dest.writeString(this.updateDate);
        dest.writeString(this.intro);
        dest.writeTypedList(this.playSources);
    }

    public MovieDetail() {
    }

    protected MovieDetail(Parcel in) {
        this.id = in.readLong();
        this.channel = in.readString();
        this.img = in.readString();
        this.name = in.readString();
        this.sketch = in.readString();
        this.score = in.readString();
        this.alias = in.readString();
        this.director = in.readString();
        this.starring = in.readString();
        this.type = in.readString();
        this.types = in.readString();
        this.area = in.readString();
        this.language = in.readString();
        this.showDate = in.readString();
        this.duration = in.readString();
        this.updateDate = in.readString();
        this.intro = in.readString();
        this.playSources = in.createTypedArrayList(PlaySource.CREATOR);
    }

    public static final Parcelable.Creator<MovieDetail> CREATOR = new Parcelable.Creator<MovieDetail>() {
        @Override
        public MovieDetail createFromParcel(Parcel source) {
            return new MovieDetail(source);
        }

        @Override
        public MovieDetail[] newArray(int size) {
            return new MovieDetail[size];
        }
    };
}
