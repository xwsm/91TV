package com.owen.tv91.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class PlaySource implements Parcelable {

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeList(this.playUrls);
    }

    public PlaySource() {
    }

    protected PlaySource(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.type = in.readString();
        this.playUrls = new ArrayList<PlayUrl>();
        in.readList(this.playUrls, PlayUrl.class.getClassLoader());
    }

    public static final Parcelable.Creator<PlaySource> CREATOR = new Parcelable.Creator<PlaySource>() {
        @Override
        public PlaySource createFromParcel(Parcel source) {
            return new PlaySource(source);
        }

        @Override
        public PlaySource[] newArray(int size) {
            return new PlaySource[size];
        }
    };
}
