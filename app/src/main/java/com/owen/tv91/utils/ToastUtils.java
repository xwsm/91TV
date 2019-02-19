package com.owen.tv91.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

/**
 * toast
 */
public class ToastUtils {
    private static final long LENGTH_SHORT_TIME = 2000;

    private static Toast toast = null;

    private static Handler mHandler = null;

    public static int mDuration = Toast.LENGTH_LONG;
    
    private static long mCustomDuration = -1;

    public static String mMsg = null;
    
    public static Context mContext;

    public static void initToast(Context context) {
        if (toast == null) {
            mContext = context != null ? context.getApplicationContext() : null;
            mHandler = new Handler(Looper.getMainLooper());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mContext != null) {
                        toast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                    }
                }
            });
        }
    }

    private static void show(final int duration, final String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        if (null == mContext) {
            return;
        }
        mDuration = duration;
        mMsg = msg;
        mHandler.removeCallbacks(showRunnable);
        mHandler.postDelayed(showRunnable, 100);
    }

    private static final Runnable showRunnable = new Runnable() {

        @Override
        public void run() {
            if (toast != null) {
                toast.setDuration(mDuration);
                toast.setText(mMsg);
                toast.show();
                
                if(mCustomDuration <= 0) {
                    if(mCustomDuration == 0) {
                        hide();
                    }
                    return;
                }
                
                if(mCustomDuration > LENGTH_SHORT_TIME) {
                    mHandler.postDelayed(showRunnable, LENGTH_SHORT_TIME);
                    mCustomDuration -= LENGTH_SHORT_TIME;
                } else {
                    mHandler.postDelayed(showRunnable, mCustomDuration);
                    mCustomDuration = 0;
                }
            }
        }
    };
    
    public static void hide() {
        if(null != toast) {
            toast.cancel();
            mDuration = Toast.LENGTH_LONG;
            mCustomDuration = -1;
        }
    }

    public static void showShortToast(int resID) {
        show(Toast.LENGTH_SHORT, mContext.getString(resID));
    }

    public static void showShortToast(String msg) {
        show(Toast.LENGTH_SHORT, msg);
    }

    public static void showLongToast(int resID) {
        show(Toast.LENGTH_LONG, mContext.getString(resID));
    }

    public static void showLongToast(String msg) {
        show(Toast.LENGTH_LONG, msg);
    }
    
    public static void showToast(int resID, long customDuration) {
        mCustomDuration = customDuration;
        show(Toast.LENGTH_LONG, mContext.getString(resID));
    }
    
    public static void showToast(String msg, long customDuration) {
        mCustomDuration = customDuration;
        show(Toast.LENGTH_LONG, msg);
    }
}

