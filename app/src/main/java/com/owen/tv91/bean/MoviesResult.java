package com.owen.tv91.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/2/18
 */
public class MoviesResult implements Parcelable {

    public List<Movie> content;

    public boolean last;

    public int totalPages;

    public int totalElements;

    public int numberOfElements;

    public int size;

    public int number;

    public boolean first;

    public boolean empty;


    public List<Movie> getContent() {
        return content;
    }

    public void setContent(List<Movie> content) {
        this.content = content;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.content);
        dest.writeByte(this.last ? (byte) 1 : (byte) 0);
        dest.writeInt(this.totalPages);
        dest.writeInt(this.totalElements);
        dest.writeInt(this.numberOfElements);
        dest.writeInt(this.size);
        dest.writeInt(this.number);
        dest.writeByte(this.first ? (byte) 1 : (byte) 0);
        dest.writeByte(this.empty ? (byte) 1 : (byte) 0);
    }

    public MoviesResult() {
    }

    protected MoviesResult(Parcel in) {
        this.content = in.createTypedArrayList(Movie.CREATOR);
        this.last = in.readByte() != 0;
        this.totalPages = in.readInt();
        this.totalElements = in.readInt();
        this.numberOfElements = in.readInt();
        this.size = in.readInt();
        this.number = in.readInt();
        this.first = in.readByte() != 0;
        this.empty = in.readByte() != 0;
    }

    public static final Parcelable.Creator<MoviesResult> CREATOR = new Parcelable.Creator<MoviesResult>() {
        @Override
        public MoviesResult createFromParcel(Parcel source) {
            return new MoviesResult(source);
        }

        @Override
        public MoviesResult[] newArray(int size) {
            return new MoviesResult[size];
        }
    };
}
