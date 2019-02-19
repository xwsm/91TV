package com.owen.player.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.owen.player.R;

import java.io.Closeable;
import java.util.Locale;

/**
 * Created by owen on 16/5/6.
 */
public class EPUtils {

    public static int getScreenHeight(Context context) {
        return getDisplayMetrics(context).heightPixels;
    }

    public static int getScreenWidth(Context context) {
        return getDisplayMetrics(context).widthPixels;
    }

    public static DisplayMetrics getDisplayMetrics(Context context){
        DisplayMetrics dm = null;
        if(null != context){
            dm = context.getResources().getDisplayMetrics();
        }
        return dm;
    }

    public static String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;

        if (hours > 0) {
            return String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
    }

    /**
     * 获取VersionName
     *
     * @return 当前应用的VersionName
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            android.content.pm.PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getVersionCode(Context ctx) {
        int version = 0;
        try {
            version = ctx.getPackageManager().getPackageInfo(ctx.getApplicationInfo().packageName, 0).versionCode;
        } catch (Exception e) {
            EPLog.e("getVersionInt", e);
        }
        return version;
    }

    public static String getDataDir(Context context, String dir) {
        return context.getDir(dir, Context.MODE_PRIVATE).getAbsolutePath();
    }

    public static String getDataDir(Context ctx) {
        ApplicationInfo ai = ctx.getApplicationInfo();
        if (ai.dataDir != null)
            return fixLastSlash(ai.dataDir);
        else
            return "/data/data/" + ai.packageName + "/";
    }

    public static String fixLastSlash(String str) {
        String res = str == null ? "/" : str.trim() + "/";
        if (res.length() > 2 && res.charAt(res.length() - 2) == '/')
            res = res.substring(0, res.length() - 1);
        return res;
    }

    public static void closeSilently(Closeable c) {
        if (c == null)
            return;
        try {
            c.close();
        } catch (Throwable t) {
            EPLog.e("fail to close", t);
        }
    }


    /**
     * Toast 弹出框
     *
     * @param context
     * @param text
     */
    public static void showToast(Context context, String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * Toast 弹出框
     *
     * @param context
     * @param textId
     */
    public static void showToast(Context context, int textId) {
        Toast toast = Toast.makeText(context, textId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static Dialog createCommonDialog(Context context, String content, String btn1Text, String btn2Text, final DialogInterface.OnClickListener listener) {
        final Dialog commonDialog = new Dialog(context, R.style.EduTvPlayerDialogCommonStyle);
        commonDialog.setContentView(R.layout.edu_tvplayer_dialog_common_layout);
        TextView contentTextView = (TextView) commonDialog.findViewById(R.id.dialog_content);
        Button btn1 = (Button) commonDialog.findViewById(R.id.dialog_btn1);
        Button btn2 = (Button) commonDialog.findViewById(R.id.dialog_btn2);

        contentTextView.setText(Html.fromHtml(content));
        if (!TextUtils.isEmpty(btn1Text)) {
            btn1.setText(btn1Text);
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(commonDialog, 0);
                }
            });
        } else {
            btn1.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(btn2Text)) {
            btn2.setText(btn2Text);
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(commonDialog, 1);
                }
            });
        } else {
            btn2.setVisibility(View.GONE);
        }

        return commonDialog;
    }

    /**
     *  为目录加载权限
     *  */
    public static void chmodPath(String permission, String path) {
        try {
            Runtime.getRuntime().exec("chmod " + permission + " " + path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * a method to save global data
     *
     * @param mContext
     * @param preferenceKey
     *            key
     * @param preferenceValue
     *            value
     */
    public static void savePreference(Context mContext, String preferenceKey, String preferenceValue) {
        SharedPreferences preference = mContext.getSharedPreferences("eduPreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(preferenceKey, preferenceValue);
        edit.apply();
    }

    public static void savePreference(Context mContext, String preferenceKey, int preferenceValue) {
        SharedPreferences preference = mContext.getSharedPreferences("eduPreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preference.edit();
        edit.putInt(preferenceKey, preferenceValue);
        edit.apply();
    }

    /**
     * a method to get global data
     * @return
     */
    public static String getPreferences(Context mContext, String preferenceKey) {
        SharedPreferences prefs = mContext.getSharedPreferences("eduPreference", Context.MODE_PRIVATE);
        return prefs.getString(preferenceKey, "");
    }

    /**
     * a method to save global data
     *
     * @param mContext
     * @param preferenceKey
     *            key
     * @param preferenceValue
     *            value
     */
    public static void savePreference(Context mContext, String preferenceKey, Boolean preferenceValue) {
        SharedPreferences preference = mContext.getSharedPreferences("eduPreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preference.edit();
        edit.putBoolean(preferenceKey, preferenceValue);
        edit.apply();
    }

    public static boolean getPreferences(Context mContext, String preferenceKey, boolean defValue) {
        SharedPreferences prefs = mContext.getSharedPreferences("eduPreference", Context.MODE_PRIVATE);
        return prefs.getBoolean(preferenceKey, defValue);
    }


    public static int getPreferences(Context mContext, String preferenceKey, int defValue) {
        SharedPreferences prefs = mContext.getSharedPreferences("eduPreference", Context.MODE_PRIVATE);
        return prefs.getInt(preferenceKey, defValue);
    }

    public static void removePreferences(Context mContext, String prefernceKey) {
        SharedPreferences prefs = mContext.getSharedPreferences("eduPreference", Context.MODE_PRIVATE);
        prefs.edit().remove(prefernceKey).apply();
    }

    public static void clearPreferences(Context mContext, String prefernceKey) {
        SharedPreferences prefs = mContext.getSharedPreferences("eduPreference", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}
