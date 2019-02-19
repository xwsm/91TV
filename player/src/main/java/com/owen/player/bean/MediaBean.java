package com.owen.player.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 视频列表中的Media实体对象
 * @author zsq
 *
 */
public class MediaBean implements Parcelable {

	/** 数据索引*/
	public String id;
	
	/** 视频名称 */
	public String name;
	
	/** 视频播放url地址 */
	public String playUrl;
	/** 视频播放集名称 */
	public String playName;
	
	/** 
	 *  是否有播放权限(默认为有权限状态)
	 *  “0”代表有权限
	 *  “-1”代表无权限
	 * */
	public String isBuy = "0";

	/** 是否播放前贴片广告 */
	public boolean isPlayAd = false;
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.id);
		dest.writeString(this.name);
		dest.writeString(this.playUrl);
		dest.writeString(this.playName);
		dest.writeString(this.isBuy);
		dest.writeByte(this.isPlayAd ? (byte) 1 : (byte) 0);
	}

	public MediaBean() {
	}

	protected MediaBean(Parcel in) {
		this.id = in.readString();
		this.name = in.readString();
		this.playUrl = in.readString();
		this.playName = in.readString();
		this.isBuy = in.readString();
		this.isPlayAd = in.readByte() != 0;
	}

	public static final Creator<MediaBean> CREATOR = new Creator<MediaBean>() {
		@Override
		public MediaBean createFromParcel(Parcel source) {
			return new MediaBean(source);
		}

		@Override
		public MediaBean[] newArray(int size) {
			return new MediaBean[size];
		}
	};

}
