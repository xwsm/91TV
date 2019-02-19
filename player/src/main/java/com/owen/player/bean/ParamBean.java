package com.owen.player.bean;

import java.io.Serializable;

public class ParamBean implements Serializable {
	private static final long serialVersionUID = -1167442934356062468L;
	
	/** 
	 * 传递参数：KEY;
	 * 代表：指定从列表中的播放位置
	 * */
	public static final String KEY_PLAY_INDEX = "playIndex";
	
	/** 
	 * 传递参数：KEY;
	 * 代表：当前已播放时长
	 * */
	public static final String KEY_PLAYED_TIME = "playedTime";
	
	/** 
	 * 传递参数：KEY;
	 * 代表：视频总时长
	 * */
	public static final String KEY_DURATION_TIME = "durationTime";
	
	/** 
	 * 传递参数：KEY;
	 * 代表：当前播放视频MediaBean实体对象
	 * */
	public static final String KEY_MEDIA_OBJECT = "mediaObject";
	
	///////////////////////////////////////////////////////////////////////////////////////////

	public static final String KEY_PLAY_HISTORY_KEY_ID = "keyId";

	public static final String KEY_PLAY_HISTORY_SUBKEY_ID = "subKeyId";
	
	/**
	 * 传递参数：KEY;
	 * 代表：huanId
	 */
	public static final String KEY_PLAY_HISTORY_HUANID = "huanId";
	
	/**
	 * 传递参数：KEY;
	 * 代表: 产品包id
	 */
	public static final String KEY_PLAY_HISTORY_PID = "pid";
	
	/**
	 * 传递参数：KEY;
	 * 代表: 产品包名称
	 */
	public static final String KEY_PLAY_HISTORY_PNAME = "pname";
	
	/**
	 * 传递参数：KEY;
	 * 代表: 产品包所属分类的classKeyId
	 */
	public static final String KEY_PLAY_HISTORY_CLASSKEYID = "classKeyId";
	
	/**
	 * 传递参数：KEY;
	 * 代表: 产品包所属分类的parentKeyId 实际就是大分类的classkeyid
	 */
	public static final String KEY_PLAY_HISTORY_PARENTKEYID = "parentKeyId";
	
	/**
	 * 传递参数：KEY;
	 * 代表: 产品包所属分类的classId
	 */
	public static final String KEY_PLAY_HISTORY_CLASSID = "classId";
	
	/**
	 * 传递参数：KEY;
	 * 代表: 产品包所属分类的className
	 */
	public static final String KEY_PLAY_HISTORY_CLASSNAME = "classNamess";
	
	/**
	 * 传递参数：KEY;
	 * 代表: 当前所属哪个app应用
	 * 对应值：kzenglish/student/audioStory/ott_child/finance_android/kongzhongyoueryuan
	 */
	public static final String KEY_PLAY_HISTORY_CLIENTCODE = "clientCode";
	
	/**
	 * 传递参数：KEY;
	 * 代表: 当前音视频id
	 */
	public static final String KEY_PLAY_HISTORY_MUSICVIDEOID = "musicVideoId";
	
	/**
	 * 传递参数：KEY;
	 * 代表: 当前音视频名称
	 */
	public static final String KEY_PLAY_HISTORY_MUSICVIDEONAME = "musicVideoName";
	/**
	 * 传递参数：KEY;
	 * 代表: 播放结束时的时间
	 */
	public static final String KEY_PLAY_HISTORY_PLAYTIME = "playTime";

	/**
	 * 传递参数：KEY;
	 * 代表: 下个音视频id
	 */
	public static final String KEY_PLAY_HISTORY_NEXTMUSICVIDEOID = "nextMusicVideoId";
	
	/**
	 * 传递参数：KEY;
	 * 代表: 下个音视频名称
	 */
	public static final String KEY_PLAY_HISTORY_NEXTMUSICVIDEONAME = "nextMusicVideoName";
	
	/**
	 * 传递参数：KEY;
	 * 代表: 历史记录上报接口地址
	 */
	public static final String KEY_UPLOAD_HISTORY_URL = "UploadHistoryUrl";
	
	/**
	 * 传递参数：KEY;
	 * 代表: 是否上报播放历史记录
	 */
	public static final String KEY_PLAY_HISTORY_ISUPLOADHISTORY = "isUploadHistory";
	
	/**
	 * 传递参数：KEY;
	 * 代表: 是否上报播放历史记录
	 */
	public static final String KEY_PLAY_HISTORY_ISUPLOAD_PLAY_TIME = "isUploadPlayTime";

	/**
	 * 传递参数：KEY;
	 * 代表: 是否使用硬解码
	 */
	public static final String KEY_IS_HARDWARE_DECODER = "isHardwareDecoder";
	/**
	 * 传递参数：KEY;
	 * 代表: 息屏时是否退出播放器
	 */
	public static final String KEY_IS_SCREENOFF_EXIT = "isScreenOffExit";

	/**
	 * 传递参数：KEY;
	 * 代表: 是否上报长虹任务
	 */
	public static final String KEY_PLAY_IS_UPLOAD_CH= "isUploadCH";

	/**
	 * 传递参数：KEY;
	 * 代表: 是否请求播放地址
	 */
	public static final String KEY_IS_REQUST_PLAY_ADDRESS= "isRequstPlayAddress";

	/**
	 * 传递参数：KEY;
	 * 代表: 请求播放地址的url
	 */
	public static final String KEY_REQUST_PLAY_ADDRESS_URL= "requstPlayAddressUrl";
}
