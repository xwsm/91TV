package com.owen.player.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.owen.player.R;
import com.owen.player.utils.EPUtils;


/**
 * Created by owen on 16/5/10.
 */
public class ProgressPopupWindow extends PopupWindow {
    private static final String TAG = ProgressPopupWindow.class.getSimpleName() + " %s";
    private Context mContext;
    private LayoutInflater mInflater;
    private SeekBar mProgress;
    private TextView mCurrentTime;
    private TextView mEndTime;
    private TextView mMediaName;
    
    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mCurrentTime.post(new Runnable() {
                @Override
                public void run() {
                    mCurrentTime.setX(getXPosition(mProgress));
                }
            });
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private float getXPosition(SeekBar seekBar){
        float offset = seekBar.getThumbOffset();
        float val = (((float)seekBar.getProgress() * (seekBar.getWidth() - 2 * offset)) / seekBar.getMax());
        int textWidth = mCurrentTime.getWidth();
        float textCenter = textWidth / 2.0f;
        float newX = val + offset - textCenter;

        return seekBar.getX() + Math.max(newX, 0);
    }
    
    public ProgressPopupWindow(Context context, boolean focusable) {
        mContext = context;
        setContentView(initView());
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(focusable);
    }

    private View initView() {
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View progressview = mInflater.inflate(R.layout.edu_tvplayer_progress_layout, null);
        mProgress = (SeekBar) progressview.findViewById(R.id.seek_bar_progress);
        mProgress.setMax(1000);
        mProgress.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        mCurrentTime = (TextView) progressview.findViewById(R.id.tv_media_curr_position);
        mEndTime = (TextView) progressview.findViewById(R.id.tv_media_duration);
        mMediaName = (TextView) progressview.findViewById(R.id.tv_media_name);
        return progressview;
    }

    public int updateProgress(int position, int duration, int percent) {
        if (mProgress != null) {
            percent *= 10;
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 1000L * position / duration;
                pos = Math.min(pos, mProgress.getMax());
                percent = Math.max(percent, (int)pos);
//                EPLog.e(TAG, "percent="+percent + ", pos="+pos);
                mProgress.setSecondaryProgress(percent);
                mProgress.setProgress( (int) pos);
            }
        }

        if (mEndTime != null)
            mEndTime.setText(EPUtils.stringForTime(duration));
        if (mCurrentTime != null)
            mCurrentTime.setText(EPUtils.stringForTime(position));

        return position;
    }
    
    public int seekToProgress(int duration, boolean isForward) {
        if(duration <= 0)
            return 0;
        long seekProgress = (int)(duration * 0.01f * 1000L) / duration;
        long progress = mProgress.getProgress() + (isForward ? seekProgress : -seekProgress);
        if(progress > 1000) {
            progress = 1000;
        }
        else if (progress < 0) {
            progress = 0;
        }
        mProgress.setSecondaryProgress(0);
        mProgress.setProgress((int)progress);
        long newposition = duration * progress / 1000L;
        if (mCurrentTime != null)
            mCurrentTime.setText(EPUtils.stringForTime( (int) newposition));
        
        return (int) progress;
    }
    
    public void setMediaName(String name) {
        if(null != mMediaName) {
            mMediaName.setText(name);
        }
    }
    
    public int getProgress() {
        if(null != mProgress) {
            return mProgress.getProgress();
        }
        return 0;
    }
}
