package com.owen.player.widget;

import android.view.View;
import android.view.Window;

import com.owen.player.bean.MediaBean;

import java.util.List;

/**
 * Created by owen on 16/5/5.
 */
public interface EduIMediaController {
    void setWindow(Window window);
    
    void setAnchorView(View view);

    void setEnabled(boolean enabled);
    
    void setMediaPlayer(EduIVideoView player);
    
    void setPlayList(List<MediaBean> list);
    
    void hide();
    
    void hideProgress();
    
    void hidePlayList();

    void hideAdTime();
    
    boolean isShowing();

    boolean isShowingProgress();
    
    boolean isShowingPlayList();

    boolean isShowingAdTime();

    void show(int timeout);

    void show();
    
    void showProgress(int timeout);
    
    void showProgress();
    
    void showPlayList(int timeout);
    
    void showPlayList();

    void showAdTime();
    
    void fastReverse(); // 快退
    
    void fastForward(); // 快进
    
    void release(); // 释放资源
}
