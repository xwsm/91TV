package com.owen.player;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.owen.player.bean.MediaBean;
import com.owen.player.bean.ParamBean;
import com.owen.player.utils.EPLog;
import com.owen.player.utils.EPUtils;
import com.owen.player.widget.EduIMediaController;
import com.owen.player.widget.EduIVideoView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;


/**
 * Created by owen on 16/8/4.
 */
public class EduPlayerFragment extends Fragment implements EduIVideoView.OnEduMediaListener {
    private static final String TAG = EduPlayerFragment.class.getSimpleName() + " %s";

    private static final String KEY_CONTROLLER_ENABLED = "key_controller_enabled";

    private static final int MSG_UPLOAD_PLAY = 1;

    private EduIVideoView mVideoView;

    private ImageView mPlayerBg;

    private ViewGroup mLoadingView;

    private EduIMediaController mMediaController;

    private PlayerSettings mPlayerSettings;

    private Dialog mErrorDialog;

    private Dialog mNoPermissionDialog;

    private boolean mEnabled = true;

    //正常播放器退到后台就kill掉释放资源，休息提醒的时候退到后台
    private boolean mIsBackGound = false;

    private OnFinishListener mOnFinishListener;

    private Handler mHandler = new Handler() {
        int pos;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPLOAD_PLAY:
                    pos = uploadPlayTimeBy30();
                    if (pos / 1000 < 30) {
                        msg = obtainMessage(MSG_UPLOAD_PLAY);
                        sendMessageDelayed(msg, 1000);
                    }
                    break;
            }
        }
    };

    public static EduPlayerFragment create(Context context) {
        return create(context, true);
    }

    public static EduPlayerFragment create(Context context, boolean isControllerEnabled) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_CONTROLLER_ENABLED, isControllerEnabled);
        return (EduPlayerFragment) Fragment.instantiate(context, EduPlayerFragment.class.getName(), bundle);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() instanceof OnFinishListener) {
            mOnFinishListener = (OnFinishListener) getActivity();
        }
        Bundle bundle = getArguments();
        if (null != bundle) {
            mEnabled = bundle.getBoolean(KEY_CONTROLLER_ENABLED, true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EPLog.i(TAG, "onCreateView...");
        return inflater.inflate(R.layout.edu_tvplayer_video_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        EPLog.i(TAG, "onViewCreated...");
        EventBus.getDefault().register(this);
        mPlayerSettings = PlayerSettings.getInstance(getContext());
        if (null != savedInstanceState) {
            mEnabled = savedInstanceState.getBoolean(KEY_CONTROLLER_ENABLED, true);
            if (null != mPlayerSettings) {
                mPlayerSettings.restoreState(savedInstanceState);
            }
        }
        initView(view);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_CONTROLLER_ENABLED, mEnabled);
        if (null != mPlayerSettings) {
            mPlayerSettings.saveState(outState);
        }
        super.onSaveInstanceState(outState);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayerEnterBackGroundEvent(PlayerEnterBackGroundEvent event) {
        mIsBackGound = event.isBackGround();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EPLog.i(TAG, "onActivityCreated...");
        initPlayer();
    }

    @Override
    public void onResume() {
        EPLog.i(TAG, "onResume...");
        if (null != mVideoView && mVideoView.isInPlaybackState()) {
            mVideoView.start();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("ygx", "onPause");
        if (null != mVideoView && mVideoView.isPlaying()) {
            mVideoView.pause();
        }
        if (!mIsBackGound) {
            killPlayer();
        }

    }

    @Override
    public void onStop() {
        EPLog.i(TAG, "onStop...");
        super.onStop();
        if (!mIsBackGound) killPlayer();
    }

    private void killPlayer() {
        Log.i("ygx", "killPlayer");
        if (null != mErrorDialog && mErrorDialog.isShowing()) {
            mErrorDialog.cancel();
        }
        if (null != mNoPermissionDialog && mNoPermissionDialog.isShowing()) {
            mNoPermissionDialog.cancel();
        }
        if (null != mVideoView && mVideoView.isPlaying()) {
            mVideoView.pause();
        }

        mHandler.removeMessages(MSG_UPLOAD_PLAY);

        if (null != mVideoView) {
            if (!mVideoView.isBackgroundPlayEnabled()) {
                Log.i("ygx", "here1");
                mVideoView.releaseBack();
            } else {
                mVideoView.enterBackground();
                Log.i("ygx", "here2");
            }
            mVideoView = null;
        }

        if (null != mMediaController) {
            mMediaController.release();
            mMediaController = null;
        }
        mErrorDialog = null;
        mNoPermissionDialog = null;
        mOnFinishListener = null;
        getActivity().finish();
    }

    @Override
    public void onDestroy() {
        Log.i("ygx", "onDestroy");
        EPLog.i(TAG, "onDestroy...");
        EventBus.getDefault().unregister(this);
        killPlayer();
        super.onDestroy();
    }

    public void setControllerEnabled(boolean enabled) {
        this.mEnabled = enabled;
        if (null != mMediaController) {
            mMediaController.setEnabled(mEnabled);
        }
    }

    private void initView(View root) {
        mLoadingView = (ViewGroup) root.findViewById(R.id.edu_tvplayer_loading);
        mPlayerBg = root.findViewById(R.id.edu_tvplayer_bg);
        mMediaController = (EduIMediaController) root.findViewById(R.id.edu_tvplayer_controller);
        mMediaController.setWindow(getActivity().getWindow());
        mMediaController.setPlayList(mPlayerSettings.getMediaList());
        mMediaController.setEnabled(mEnabled);
        mVideoView = (EduIVideoView) root.findViewById(R.id.video_view);
        mVideoView.setEduMediaController(mMediaController);
    }

    private void initPlayer() {
        mVideoView.setOnEduMediaListener(this);

        if (Build.VERSION.SDK_INT >= 21) {
            EPLog.i("supported ABIS " + Arrays.toString(Build.SUPPORTED_ABIS));
        } else {
            EPLog.i("supported ABIS [" + Build.CPU_ABI + ", " + Build.CPU_ABI2 + "]");
        }

        if (mPlayerSettings.isAndroidPlayer()) {
            mVideoView.playVideoWithBean(mPlayerSettings.getCurrMedia());
        } else {
            mLoadingView.setVisibility(View.VISIBLE);
            mVideoView.initVideoLib();// init player
            mVideoView.playVideoWithBean(mPlayerSettings.getCurrMedia());

//            EPLibraryUtil.initialize(getContext().getApplicationContext(), new EPLibraryUtil.InitLibraryCallback() {
//                @Override
//                public void onInitStart() {
//                    mLoadingView.setVisibility(View.VISIBLE);
//                }
//
//                @Override
//                public void onInitFinish(boolean isSucceed) {
//                    if(isSucceed) {
//                        mVideoView.initVideoLib();
//                        mVideoView.playVideoWithBean(mPlayerSettings.getCurrMedia());
//                    } else {
//                        showErrorDialog();
//                    }
//                }
//            });
        }
    }

    private void showErrorDialog() {
        EPLog.d(TAG, "showErrorDialog1");
        if (null == getActivity() || getActivity().isFinishing())
            return;
        if (null == mErrorDialog && isAdded()) {
            EPLog.d(TAG, "mErrorDialog");
            mErrorDialog = EPUtils.createCommonDialog(
                    getActivity(),
                    getString(R.string.edu_tvplayer_load_media_error),
                    getString(R.string.edu_tvplayer_ok_btn),
                    "",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (null != mMediaController) {
                                mMediaController.hide();
                            }
                            dialog.cancel();
                            if (null != mOnFinishListener) {
                                mOnFinishListener.onFinish(false);
                            }
                        }
                    }
            );
            //mErrorDialog.setCancelable(true);长虹提出影响用户体验2018年09月04日改成一键退出
            mErrorDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                    switch (i) {
                        case KeyEvent.KEYCODE_BACK:
                        case KeyEvent.KEYCODE_ESCAPE:
                            dialogInterface.dismiss();
                            killPlayer();
                    }
                    return false;
                }
            });
        }
        if (mVideoView.isPlaying()) mVideoView.pause();
        if (null != mErrorDialog)
            mErrorDialog.show();
        mLoadingView.setVisibility(View.GONE);
    }

    private void showNoPermissionDialog() {
        if (null == getActivity() || getActivity().isFinishing())
            return;
        if (null == mNoPermissionDialog && isAdded()) {
            mNoPermissionDialog = EPUtils.createCommonDialog(
                    getActivity(),
                    getString(R.string.edu_tvplayer_no_permission_play),
                    getString(R.string.edu_tvplayer_ok_btn),
                    "",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mPlayerSettings.getPayJumpMode() == PlayerSettings.PAY_JUMP_MODE_ACTIVITY) {
                                Intent intent = new Intent();
                                intent.setClassName(getContext().getPackageName(), mPlayerSettings.getPayClassName());
                                intent.putExtras(mPlayerSettings.getPayBundle());
                                startActivity(intent);
                            }
                            if (null != mMediaController) {
                                mMediaController.hide();
                            }
                            dialog.cancel();
                            if (null != mOnFinishListener) {
                                mOnFinishListener.onFinish(mPlayerSettings.getPayJumpMode() == PlayerSettings.PAY_JUMP_MODE_RESULT);
                            }
                        }
                    }
            );
//            mNoPermissionDialog.setCancelable(true);长虹提出影响用户体验2018年09月04日改一键退出
            mNoPermissionDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                    switch (i) {
                        case KeyEvent.KEYCODE_BACK:
                        case KeyEvent.KEYCODE_ESCAPE:
                            dialogInterface.dismiss();
                            killPlayer();
                    }
                    return false;
                }
            });
        }
        if (mVideoView.isPlaying()) mVideoView.pause();
        if (null != mNoPermissionDialog)
            mNoPermissionDialog.show();
        mLoadingView.setVisibility(View.GONE);
    }

    /**
     * 获取回传播放数据
     */
    public Intent getResultIntent() {
        Intent data = new Intent();
        data.putExtra(ParamBean.KEY_PLAY_INDEX, mPlayerSettings.getPlayIndex());
        if (null != mVideoView) {
            uploadPlayTime(); //上报播放时长
            data.putExtra(ParamBean.KEY_PLAYED_TIME, mVideoView.getCurrentPosition());
            data.putExtra(ParamBean.KEY_DURATION_TIME, mVideoView.getDuration());
        }
        if (null != mPlayerSettings.getCurrMedia()) {
            data.putExtra(ParamBean.KEY_MEDIA_OBJECT, mPlayerSettings.getCurrMedia());
        }
        return data;
    }

    /**
     * 上报播放时长
     */
    private void uploadPlayTime() {
//        if (null != mVideoView && null != mPlayerSettings && !mVideoView.isPlayingAd()) {
//            EPUploadUtil.getInstance().uploadPlayTime(
//                    getContext(),
//                    mVideoView.getCurrentPosition() / 1000,
//                    mVideoView.getDuration() / 1000,
//                    mPlayerSettings.getPlayIndex(),
//                    mPlayerSettings.getMediaList());
//        }
    }

    /**
     * 上报播放任务
     */
    private int uploadPlayTimeBy30() {
//        if (null != mVideoView && null != mPlayerSettings && !mVideoView.isPlayingAd()) {
//            int pos = mVideoView.getCurrentPosition();
//            EPUploadUtil.getInstance().uploadPlayTimeBy30(pos / 1000,
//                    mPlayerSettings.getPlayIndex(), mPlayerSettings.getMediaList());
//            return pos;
//        }
        return 0;
    }

    @Override
    public void onPrepared(EduIVideoView mp) {
        EPLog.d(TAG, "onPrepared...");
        mHandler.sendEmptyMessage(MSG_UPLOAD_PLAY);
        mLoadingView.setVisibility(View.GONE);
        mPlayerBg.setVisibility(View.GONE);
        if (null != mNoPermissionDialog) mNoPermissionDialog.dismiss();
        if (null != mErrorDialog) mErrorDialog.dismiss();
        if (null != mMediaController) mMediaController.hide();
    }

    @Override
    public void onCompletion(EduIVideoView mp) {
        EPLog.d(TAG, "onCompletion...");
        uploadPlayTime(); //上报播放时长
        mLoadingView.setVisibility(View.GONE);
        //if是列表的最后一个播完就关闭播放器
        if (mPlayerSettings.getPlayIndex() + 1 >= mPlayerSettings.getMediaList().size())
            killPlayer();
        else {
            if (null != mNoPermissionDialog) mNoPermissionDialog.dismiss();
            if (null != mErrorDialog) mErrorDialog.dismiss();
            MediaBean mediaBean = mPlayerSettings.getNextMedia();
            if (null != mediaBean && null != mVideoView) {
                mVideoView.playVideoWithBean(mediaBean);
                return;
            }
            if (null != mOnFinishListener) {
                mOnFinishListener.onFinish(false);
            }
        }
    }

    @Override
    public void onBufferingUpdate(EduIVideoView mp, int percent) {
    }

    @Override
    public void onSeekStart(EduIVideoView mp) {
        EPLog.d(TAG, "onSeekStart...");
        onBufferingStart(mp);
    }

    @Override
    public void onSeekComplete(EduIVideoView mp) {
        EPLog.d(TAG, "onSeekComplete...");
        onBufferingEnd(mp);
    }

    @Override
    public boolean onError(final EduIVideoView mp, int what, int extra) {
        EPLog.d(TAG, "onError...what=" + what + ", extra=" + extra + " mp.isPlaying()=" + mp.isPlaying());
        if (!mp.isPlaying()) {
            mLoadingView.setVisibility(View.GONE);
            showErrorDialog();
        }
        return false;
    }

    @Override
    public boolean onInfo(final EduIVideoView mp, int what, int extra) {
        EPLog.d(TAG, "onInfo...what=" + what + ", extra=" + extra + " mp.isPlaying()=" + mp.isPlaying());
        switch (what) {
            case 710: // 缓冲开始（中信湖北盒子）
            case EduIVideoView.MEDIA_INFO_BUFFERING_START: // 缓冲开始
                onBufferingStart(mp);
                break;

            case 1003: // 缓冲结束（中信湖北盒子）
            case EduIVideoView.MEDIA_INFO_VIDEO_RENDERING_START: // 音频媒体信息开始呈现
            case EduIVideoView.MEDIA_INFO_BUFFERING_END: // 缓冲结束
                onBufferingEnd(mp);
                break;

            case EduIVideoView.MEDIA_INFO_VIDEO_RESUME: // 继续播放
                mHandler.sendEmptyMessage(MSG_UPLOAD_PLAY);
                break;

            case EduIVideoView.MEDIA_INFO_VIDEO_PAUSE: // 暂停
                mHandler.removeMessages(MSG_UPLOAD_PLAY);
                break;
        }

        return true;
    }

    @Override
    public void onNoPermission(EduIVideoView mp, MediaBean bean) {
        showNoPermissionDialog();
    }

    private void onBufferingStart(final EduIVideoView mp) {
        EPLog.d("onBufferingStart  " + "mp.isPlaying:" + mp.isPlaying());
        mLoadingView.setVisibility(View.VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofFloat(mLoadingView, "alpha", 0f, 1f);
        animator.setDuration(1000);
        animator.start();
    }

    private void onBufferingEnd(EduIVideoView mp) {
//        mHintView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        boolean showDialog = (mNoPermissionDialog != null && !mNoPermissionDialog.isShowing()) || (mErrorDialog != null && !mErrorDialog.isShowing());
        if (showDialog && !mp.isPlaying())
            mp.start();
        if (null != mMediaController) {
            mMediaController.hide();
        }
    }

    public interface OnFinishListener {
        void onFinish(boolean isNoPermissionPlay);
    }

//    //tcl938板子待机后黑屏
//    public class ScreenOnBroadcastReceiver extends BroadcastReceiver {
//        private boolean isRegisterReceiver = false;
//        private Activity context;
//
//        ScreenOnBroadcastReceiver(Activity activity) {
//            context = activity;
//        }
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(intent.ACTION_SCREEN_ON)) {
//                Log.i("ygx", "ShutdownBroadcastReceiver onReceive(), Do thing!");
//                Toast.makeText(context,"关机啦啦" ,Toast.LENGTH_LONG).show();
//                mHandler.removeMessages(MSG_UPLOAD_PLAY);
//                if(null != mMediaController) {
//                    mMediaController.release();
//                    mMediaController = null;
//                }
//                if(null != mVideoView) {
//                    mVideoView.releaseBack();
//                    mVideoView = null;
//                }
//                mErrorDialog = null;
//                mNoPermissionDialog = null;
//                mOnFinishListener = null;
//            }
//        }
//
//        public void registerReceiver() {
//            if (!isRegisterReceiver && null != context) {
//                isRegisterReceiver = true;
//                IntentFilter filter = new IntentFilter();
//                filter.addAction("android.intent.action.SCREEN_ON");
//                context.registerReceiver(this, filter);
//            }
//        }
//
//        public void unRegisterReceiver() {
//            if (isRegisterReceiver && null != context) {
//                isRegisterReceiver = false;
//                context.unregisterReceiver(this);
//                context = null;
//            }
//        }
//    }
}