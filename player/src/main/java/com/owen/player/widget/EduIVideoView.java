package com.owen.player.widget;

import com.owen.player.bean.MediaBean;

/**
 * Created by owen on 16/5/5.
 */
public interface EduIVideoView {

    int MEDIA_INFO_UNKNOWN = 1;
    int MEDIA_INFO_STARTED_AS_NEXT = 2;
    int MEDIA_INFO_VIDEO_RENDERING_START = 3;
    int MEDIA_INFO_VIDEO_TRACK_LAGGING = 700;
    int MEDIA_INFO_BUFFERING_START = 701;
    int MEDIA_INFO_BUFFERING_END = 702;
    int MEDIA_INFO_NETWORK_BANDWIDTH = 703;
    int MEDIA_INFO_BAD_INTERLEAVING = 800;
    int MEDIA_INFO_NOT_SEEKABLE = 801;
    int MEDIA_INFO_METADATA_UPDATE = 802;
    int MEDIA_INFO_TIMED_TEXT_ERROR = 900;
    int MEDIA_INFO_UNSUPPORTED_SUBTITLE = 901;
    int MEDIA_INFO_SUBTITLE_TIMED_OUT = 902;

    int MEDIA_INFO_VIDEO_ROTATION_CHANGED = 10001;
    int MEDIA_INFO_AUDIO_RENDERING_START = 10002;

    int MEDIA_INFO_VIDEO_PAUSE = 11001;
    int MEDIA_INFO_VIDEO_RESUME = 11002;

    int MEDIA_ERROR_UNKNOWN = 1;
    int MEDIA_ERROR_SERVER_DIED = 100;
    int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200;
    int MEDIA_ERROR_IO = -1004;
    int MEDIA_ERROR_MALFORMED = -1007;
    int MEDIA_ERROR_UNSUPPORTED = -1010;
    int MEDIA_ERROR_TIMED_OUT = -110;
    
    void initVideoListener();
    void setEduMediaController(EduIMediaController controller);
    void setOnEduMediaListener(OnEduMediaListener onEduMediaListener);
    void initVideoLib();
    void releaseBack();
    void enterBackground();
    boolean isBackgroundPlayEnabled();
    boolean isInPlaybackState();

    void    start();
    void    pause();
    int     getDuration();
    int     getCurrentPosition();
    void    seekTo(int pos);
    boolean isPlaying();
    int     getBufferPercentage();
    boolean canPause();
    boolean canSeekBackward();
    boolean canSeekForward();

    /**
     * Get the audio session id for the player used by this VideoView. This can be used to
     * apply audio effects to the audio track of a video.
     * @return The audio session, or 0 if there was an error.
     */
    int     getAudioSessionId();

    /**
     * Extends
     */
    void playVideoWithBean(MediaBean bean);

    boolean isPlayingAd();
    
    interface OnEduMediaListener {
        void onPrepared(EduIVideoView mp);
        void onCompletion(EduIVideoView mp);
        void onBufferingUpdate(EduIVideoView mp, int percent);
        void onSeekStart(EduIVideoView mp);
        void onSeekComplete(EduIVideoView mp);
        boolean onError(EduIVideoView mp, int what, int extra);
        boolean onInfo(EduIVideoView mp, int what, int extra);
        void onNoPermission(EduIVideoView mp, MediaBean bean);
    }
}
