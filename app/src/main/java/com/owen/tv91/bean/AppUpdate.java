package com.owen.tv91.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/2/27
 */
public class AppUpdate implements Parcelable {

    public long id;

    public String versionName;
    public int versionCode;
    public String downloadUrl;
    public String body;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.versionName);
        dest.writeInt(this.versionCode);
        dest.writeString(this.downloadUrl);
        dest.writeString(this.body);
    }

    public AppUpdate() {
    }

    protected AppUpdate(Parcel in) {
        this.id = in.readLong();
        this.versionName = in.readString();
        this.versionCode = in.readInt();
        this.downloadUrl = in.readString();
        this.body = in.readString();
    }

    public static final Parcelable.Creator<AppUpdate> CREATOR = new Parcelable.Creator<AppUpdate>() {
        @Override
        public AppUpdate createFromParcel(Parcel source) {
            return new AppUpdate(source);
        }

        @Override
        public AppUpdate[] newArray(int size) {
            return new AppUpdate[size];
        }
    };
}
