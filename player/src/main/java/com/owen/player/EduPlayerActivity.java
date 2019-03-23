package com.owen.player;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.widget.Toast;

import com.owen.player.event.PlayerFinishEvent;
import com.owen.player.utils.EPLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;

/**
 * Created by owen on 16/5/4.
 */
public class EduPlayerActivity extends FragmentActivity implements EduPlayerFragment.OnFinishListener {

    private static final String TAG = EduPlayerActivity.class.getSimpleName() + " %s";

    private EduPlayerFragment mFragment;

    private boolean mBackPressed = false;

    private NetworkChangeReceiver mNetworkChangeReceiver;

    private Timer mTimer = new Timer();

    private Dialog mBackDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.edu_tvplayer_video_activity);

        mFragment = EduPlayerFragment.create(this);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_content, mFragment)
                .commit();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFinishActivityEvent(PlayerFinishEvent event) {
        if(event.isFinish()) finish();
    }

    @Override
    protected void onResume() {
        EPLog.i(TAG, "onResume...");
        if (null == mNetworkChangeReceiver) {
            mNetworkChangeReceiver = new NetworkChangeReceiver(this);
        }
        mNetworkChangeReceiver.registerReceiver();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        EPLog.i(TAG, "onBackPressed...");
        showBackDialog();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME: {
                onFinish(false);
                break;
            }
        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    public void onFinish(boolean isNoPermissionPlay) {
        // 回传播放数据
        if (null != mFragment) {
            final int resultCode = isNoPermissionPlay ? PlayerSettings.RESULT_CODE_PAY : PlayerSettings.RESULT_CODE;
            setResult(resultCode, mFragment.getResultIntent());
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        if (null != mNetworkChangeReceiver) {
            mNetworkChangeReceiver.unRegisterReceiver();
            mNetworkChangeReceiver = null;
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        EPLog.i(TAG, "onStop...");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        EPLog.i(TAG, "onDestroy...");
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (null != mBackDialog && mBackDialog.isShowing()) {
            mBackDialog.cancel();
        }
    }

    private void showBackDialog() {
        Toast.makeText(this, "退出播放器！", Toast.LENGTH_SHORT).show();
        onFinish(false);
        //应乐学教育总监刘昆特殊要求，改成Toast
//        if (null == mBackDialog ) {
//            mBackDialog = EPUtils.createCommonDialog(
//                    this,
//                    "要退出吗",
//                    getString(R.string.edu_tvplayer_ok_btn),
//                    "取消",
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Log.i("ygxx",which+";;;;");
//                            if (which == 0) {
//                                onFinish(false);
//                            }
//                            dialog.cancel();
//                        }
//                    }
//            );
////            mBackDialog.setCancelable(false);
//        }
//        mBackDialog.show();
    }

    /**
     * 网络监听
     */
    private static class NetworkChangeReceiver extends BroadcastReceiver {

        private boolean isRegisterReceiver = false;
        private int lastType = -2;
        private EduPlayerActivity mActivity = null;

        NetworkChangeReceiver(EduPlayerActivity activity) {
            mActivity = activity;
        }

        @Override
        public void onReceive(Context context, Intent arg1) {
            String action = arg1.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                EPLog.i(TAG, "网络状态改变");
                // 获得网络连接服务
                ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (null != connManager) {
                    NetworkInfo info = connManager.getActiveNetworkInfo();
                    if ((info == null || !info.isAvailable()) && lastType != -1) {
                        EPLog.i(TAG, "网络连接已中断");
                        lastType = -1;
                        //退出播放
                        if (null != mActivity) {
                            mActivity.onFinish(false);
                        }
                    }
                }
            }
        }

        public void registerReceiver() {
            if (!isRegisterReceiver && null != mActivity) {
                isRegisterReceiver = true;
                IntentFilter filter = new IntentFilter();
                filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
                mActivity.registerReceiver(this, filter);
                EPLog.i(TAG, "注册网络广播接收者...");
            }
        }

        public void unRegisterReceiver() {
            if (isRegisterReceiver && null != mActivity) {
                isRegisterReceiver = false;
                mActivity.unregisterReceiver(this);
                mActivity = null;
                EPLog.i(TAG, "注销网络广播接收者...");
            }
        }
    }
}

