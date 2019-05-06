package com.owen.tv91.bean;


import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    public long id;
    public String channel; //所属频道
    public String img;
    public String name; //姓名，无双
    public String pinyinName;
    public String sketch; //简述，HD1280高清国语中字版/更新至20190114
    public String score; //评分，7.9
    public String alias; //英文名/别名，Project Gutenberg,Mo seung
    public String type; //类型，动作片
    public String showDate; //上映日期，2018
    public String updateDate; //更新日期，2018-12-21

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getShowDate() {
        return showDate;
    }

    public void setShowDate(String showDate) {
        this.showDate = showDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getPinyinName() {
        return pinyinName;
    }

    public void setPinyinName(String pinyinName) {
        this.pinyinName = pinyinName;
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
        dest.writeString(this.pinyinName);
        dest.writeString(this.sketch);
        dest.writeString(this.score);
        dest.writeString(this.alias);
        dest.writeString(this.type);
        dest.writeString(this.showDate);
        dest.writeString(this.updateDate);
    }

    public Movie() {
    }

    protected Movie(Parcel in) {
        this.id = in.readLong();
        this.channel = in.readString();
        this.img = in.readString();
        this.name = in.readString();
        this.pinyinName = in.readString();
        this.sketch = in.readString();
        this.score = in.readString();
        this.alias = in.readString();
        this.type = in.readString();
        this.showDate = in.readString();
        this.updateDate = in.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
