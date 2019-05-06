package com.owen.tv91.bean;


import android.os.Parcel;
import android.os.Parcelable;

public class PlayUrl implements Parcelable {

    public long id;
    public String name;
    public String playUrl;
    public long sourceId;
    public long movieId;
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

    public long getSourceId() {
        return sourceId;
    }

    public void setSourceId(long sourceId) {
        this.sourceId = sourceId;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.playUrl);
        dest.writeLong(this.sourceId);
        dest.writeLong(this.movieId);
        dest.writeString(this.updateDate);
    }

    public PlayUrl() {
    }

    protected PlayUrl(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.playUrl = in.readString();
        this.sourceId = in.readLong();
        this.movieId = in.readLong();
        this.updateDate = in.readString();
    }

    public static final Parcelable.Creator<PlayUrl> CREATOR = new Parcelable.Creator<PlayUrl>() {
        @Override
        public PlayUrl createFromParcel(Parcel source) {
            return new PlayUrl(source);
        }

        @Override
        public PlayUrl[] newArray(int size) {
            return new PlayUrl[size];
        }
    };
}
