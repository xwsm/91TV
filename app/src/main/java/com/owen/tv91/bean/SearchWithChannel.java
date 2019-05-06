package com.owen.tv91.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/2/20
 */
public class SearchWithChannel implements Parcelable {
    public String channel;
    public List<Movie> movies;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.channel);
        dest.writeTypedList(this.movies);
    }

    public SearchWithChannel() {
    }

    protected SearchWithChannel(Parcel in) {
        this.channel = in.readString();
        this.movies = in.createTypedArrayList(Movie.CREATOR);
    }

    public static final Parcelable.Creator<SearchWithChannel> CREATOR = new Parcelable.Creator<SearchWithChannel>() {
        @Override
        public SearchWithChannel createFromParcel(Parcel source) {
            return new SearchWithChannel(source);
        }

        @Override
        public SearchWithChannel[] newArray(int size) {
            return new SearchWithChannel[size];
        }
    };
}
