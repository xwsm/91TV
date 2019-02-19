package com.owen.player.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.owen.player.PlayerSettings;
import com.owen.player.R;
import com.owen.player.bean.MediaBean;
import com.owen.player.utils.EPLog;
import com.owen.player.utils.EPUtils;

import java.util.List;


/**
 * Created by owen on 16/5/5.
 */
public class EduIjkMediaController extends FrameLayout implements EduIMediaController {

    private static final String TAG = "MediaController %s";

    private Context mContext;

    private boolean mEnabled = true;

    private boolean mDragging = false;

    private View mAnchorView;

    private Window mWindow;

    private EduIVideoView mPlayer;

    private ProgressPopupWindow mProgressWindow;

    private ImageView mOperationHintView;

    private PlayListPopupWindow mPlayListWindow;

    private TextView mAdTimeView;

    private List<MediaBean> mMedias;

    private static final int DEFAULT_TIMEOUT = 5000;
    private static final int MSG_FADE_OUT = 1;
    private static final int MSG_UPDATE_PROGRESS = 2;
    private static final int MSG_SEEK_TO = 3;
    private static final int MSG_UPDATE_AD_TIME = 4;

    public EduIjkMediaController(Context context) {
        super(context);
        mContext = context.getApplicationContext();
        init();
    }

    public EduIjkMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context.getApplicationContext();
        init();
    }

    public EduIjkMediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context.getApplicationContext();
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EduIjkMediaController(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context.getApplicationContext();
        init();
    }

    private void init() {
        if (mEnabled) {
            setFocusable(mEnabled);
            setFocusableInTouchMode(mEnabled);
            requestFocus();
        }

        initProgressView();
        initOperationHintView();
        initPlayListView();
        initAdTimeView();
    }

    private void initOperationHintView() {
        mOperationHintView = new ImageView(mContext);
        int size = getResources().getDimensionPixelOffset(R.dimen.x106);
        LayoutParams params = new LayoutParams(size, size, Gravity.CENTER);
        addView(mOperationHintView, params);
    }

    private void initProgressView() {
        mProgressWindow = new ProgressPopupWindow(mContext, false);
    }

    private void initPlayListView() {
        mPlayListWindow = new PlayListPopupWindow(mContext, true);
        mPlayListWindow.setOnItemClickListener(new PlayListPopupWindow.OnItemClickListener() {
            @Override
            public void onItemClick(ViewGroup parent, View view, int position) {
                if (null != mMedias && null != mPlayer) {
                    mOperationHintView.setImageResource(R.color.edu_tvplayer_transparent);

                    if (TextUtils.equals("0", mMedias.get(position).isBuy))
                        PlayerSettings.getInstance(mContext).setPlayIndex(position);
                    mPlayer.playVideoWithBean(mMedias.get(position));
                    hidePlayList();
                }
            }
        });
    }

    private void initAdTimeView() {
        mAdTimeView = new TextView(mContext);
        mAdTimeView.setBackgroundResource(R.drawable.edu_tvplayer_progress_bg);
        mAdTimeView.setGravity(Gravity.CENTER);
        mAdTimeView.setTextColor(Color.WHITE);
        mAdTimeView.setVisibility(INVISIBLE);
        mAdTimeView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelOffset(R.dimen.x20));
        int size = getResources().getDimensionPixelOffset(R.dimen.x106);
        int margin = getResources().getDimensionPixelOffset(R.dimen.x20);
        LayoutParams params = new LayoutParams(size, size / 2, Gravity.RIGHT);
        params.topMargin = margin;
        params.rightMargin = margin;
        addView(mAdTimeView, params);
    }

    @Override
    public void hide() {
        hideProgress();
        hidePlayList();
        if (null != mOperationHintView && null != mPlayer && mPlayer.isPlaying()) {
            mOperationHintView.setImageResource(R.color.edu_tvplayer_transparent);
        }
    }

    @Override
    public void hideProgress() {
        if (isShowingProgress()) {
            mProgressWindow.dismiss();
//            backgroundAlpha(1.0f);
        }
    }

    @Override
    public void hidePlayList() {
        if (isShowingPlayList()) {
            mPlayListWindow.dismiss();
        }
    }

    @Override
    public void hideAdTime() {
        if (null != mAdTimeView && isShowingAdTime()) {
            mAdTimeView.setText(EPUtils.stringForTime(0));
            mAdTimeView.setVisibility(INVISIBLE);
            mHandler.removeMessages(MSG_UPDATE_AD_TIME);
        }
    }

    @Override
    public boolean isShowing() {
        return isShowingProgress() || isShowingPlayList();
    }

    @Override
    public boolean isShowingProgress() {
        return null != mProgressWindow && mProgressWindow.isShowing();
    }

    @Override
    public boolean isShowingPlayList() {
        return null != mPlayListWindow && mPlayListWindow.isShowing();
    }

    @Override
    public boolean isShowingAdTime() {
        if (null != mAdTimeView) {
            return mAdTimeView.getVisibility() == VISIBLE;
        }
        return false;
    }

    @Override
    public void setAnchorView(View view) {
        mAnchorView = view;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (mEnabled != enabled) {
            mEnabled = enabled;
            setFocusable(mEnabled);
            setFocusableInTouchMode(mEnabled);
            if (mEnabled) {
                requestFocus();
            } else {
                clearFocus();
            }
        }
    }

    @Override
    public void setMediaPlayer(EduIVideoView player) {
        mPlayer = player;
    }

    @Override
    public void setPlayList(List<MediaBean> list) {
        mMedias = list;
        if (null != mPlayListWindow) {
            mPlayListWindow.setPlaylist(list);
        }
    }

    @Override
    public void show(int timeout) {
        showProgress(timeout);
        showProgress(timeout);
    }

    @Override
    public void show() {
        showProgress();
        showPlayList();
    }

    @Override
    public void showProgress(int timeout) {
        showProgress();
        mHandler.sendEmptyMessageDelayed(MSG_FADE_OUT, timeout);
    }

    @Override
    public void showProgress() {
        if (null != mAnchorView && null != mProgressWindow && !mProgressWindow.isShowing()) {
            setProgress();
            MediaBean mediaBean = PlayerSettings.getInstance(mContext).getCurrMedia();
            mProgressWindow.setMediaName(null == mediaBean ? "" : mediaBean.name);
            mProgressWindow.showAtLocation(mAnchorView, Gravity.BOTTOM, 0, 0);
            mHandler.sendEmptyMessage(MSG_UPDATE_PROGRESS);
//            backgroundAlpha(0.2f);
        }
    }

    @Override
    public void showPlayList(int timeout) {
        showPlayList();
        mHandler.sendEmptyMessageDelayed(MSG_FADE_OUT, timeout);
    }

    @Override
    public void showPlayList() {
        if (null != mAnchorView && null != mPlayListWindow && !mPlayListWindow.isShowing()) {
            mPlayListWindow.showAtLocation(mAnchorView, Gravity.BOTTOM, 0, 0);
        }
    }

    @Override
    public void showAdTime() {
        if (null != mAdTimeView) {
            mAdTimeView.setText(null);
            mAdTimeView.setVisibility(VISIBLE);
            mHandler.sendEmptyMessage(MSG_UPDATE_AD_TIME);
        }
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha 0.0-1.0
     */
    public void backgroundAlpha(float bgAlpha) {
        if (null != mWindow) {
            WindowManager.LayoutParams lp = mWindow.getAttributes();
            lp.alpha = bgAlpha; // 0.0-1.0
            mWindow.setAttributes(lp);
        }
    }

    @Override
    public void setWindow(Window window) {
        mWindow = window;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            EPLog.i(TAG, "handleMessage...what=" + msg.what);
            int pos;
            switch (msg.what) {
                case MSG_FADE_OUT:
                    hide();
                    break;

                case MSG_UPDATE_AD_TIME:
                    pos = setAdTime();
                    if (isPlayingAd()) {
                        msg = obtainMessage(MSG_UPDATE_AD_TIME);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                    } else {
                        hideAdTime();
                    }
                    break;

                case MSG_UPDATE_PROGRESS:
                    pos = setProgress();
                    if (!mDragging && mPlayer.isPlaying() && isShowingProgress()) {
                        msg = obtainMessage(MSG_UPDATE_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;

                case MSG_SEEK_TO:
                    if (mDragging) {
                        setSeekTo();
                        msg = obtainMessage(MSG_UPDATE_PROGRESS);
                        sendMessage(msg);
                    }
                    break;
            }
        }
    };

    private int setAdTime() {
        if (null != mPlayer) {
            int position = mPlayer.getCurrentPosition();
            int duration = mPlayer.getDuration();
            EPLog.e(TAG, "position=" + position + ", duration=" + duration);
            if (duration > position) {
                mAdTimeView.setText(EPUtils.stringForTime(duration - position));
            }
            return position;
        }
        return 0;
    }

    private int setProgress() {
        if (mPlayer == null || mDragging || null == mProgressWindow) {
            return 0;
        }
        int position = mPlayer.getCurrentPosition();
        int duration = mPlayer.getDuration();
        int percent = mPlayer.getBufferPercentage();
        EPLog.e(TAG, "position=" + position + ", duration=" + duration + ", percent=" + percent);
        mProgressWindow.updateProgress(position, duration, percent);

        return position;
    }

    private void setSeekTo() {
        mDragging = false;
        mOperationHintView.setImageResource(R.color.edu_tvplayer_transparent);
        long duration = mPlayer.getDuration();
        int progress = mProgressWindow.getProgress();
        long newposition = (duration * progress) / 1000L;
        mPlayer.seekTo((int) newposition);
    }

    @Override
    public void fastForward() {
        seekProgress(true);
    }

    @Override
    public void fastReverse() {
        seekProgress(false);
    }

    private void seekProgress(boolean isForward) {
        if (null == mPlayer || null == mProgressWindow) return;
        mDragging = true;
        mHandler.removeMessages(MSG_UPDATE_PROGRESS);
        mHandler.removeMessages(MSG_SEEK_TO);
        mHandler.removeMessages(MSG_FADE_OUT);
        mOperationHintView.setImageResource(isForward ?
                R.drawable.edu_tvplayer_media_forward : R.drawable.edu_tvplayer_media_reverse);

        int duration = mPlayer.getDuration();
        mProgressWindow.seekToProgress(duration, isForward);

        mHandler.sendEmptyMessageDelayed(MSG_SEEK_TO, 1000);
    }

    private boolean isPlayingAd() {
        return null != mPlayer && mPlayer.isPlayingAd();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        EPLog.e(TAG, "onKeyDown...keyCode=" + keyCode + " ,mEnabled=" + mEnabled);

        if (mEnabled && !isPlayingAd()) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_ENTER:
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    if (null != mPlayer) {
                        if (mPlayer.isPlaying()) {
                            mOperationHintView.setImageResource(R.drawable.edu_tvplayer_media_paused);
                            mPlayer.pause();
                            showProgress();
                        } else {
                            mOperationHintView.setImageResource(R.color.edu_tvplayer_transparent);
                            mPlayer.start();
                            hide();
                        }
                        return true;
                    }
                    break;

                case KeyEvent.KEYCODE_DPAD_UP:
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (!isShowingProgress())
                        showPlayList();
                    return true;

                case KeyEvent.KEYCODE_BACK:
                case KeyEvent.KEYCODE_ESCAPE:
                    if (isShowing()) {
                        hide();
                        return true;
                    }
                    break;

                case KeyEvent.KEYCODE_DPAD_RIGHT:
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (isShowingProgress()) {
                        seekProgress(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT);
                    } else {
                        showProgress(DEFAULT_TIMEOUT);
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void release() {
        mHandler.removeMessages(MSG_FADE_OUT);
        mHandler.removeMessages(MSG_UPDATE_PROGRESS);
        mHandler.removeMessages(MSG_SEEK_TO);
        mHandler.removeMessages(MSG_UPDATE_AD_TIME);
        mHandler = null;
        hide();
        mAnchorView = null;
        mMedias = null;
        mPlayListWindow = null;
        mProgressWindow = null;
        mPlayer = null;
        mWindow = null;
    }
}
