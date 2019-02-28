package com.owen.tv91.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;

import com.owen.tv91.BuildConfig;

import java.io.File;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/2/28
 *
 * https://blog.csdn.net/zz1667654468/article/details/84568920
 */
public class InstallUtils {

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void start8Install(Context context, File file) {
        if(!context.getPackageManager().canRequestPackageInstalls()){
            //打开权限
            Uri packageURI = Uri.parse("package:" + context.getPackageName());
            //注意这个是8.0新API
            Intent intent1 = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
            context.startActivity(intent1);
            start7Install(context, file);
        }else{
            //有权限，直接安装
            start7Install(context, file);
        }
    }

    /**
     * 7.0安装应用
     */
    private static void start7Install(Context context, File file) {
        Uri apkUri = FileProvider.getUriForFile(context, "com.owen.tv91.provider", file);//在AndroidManifest中的android:authorities值
        Intent install =new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        install.setDataAndType(apkUri, "application/vnd.android.package-archive");
        context.startActivity(install);
    }

    /**
     * 普通调起安装,android 7.0一下
     * @param file
     */
    private static void installApp(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static void install(Context context, File file) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            start8Install(context, file);
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            start7Install(context, file);
        } else {
            installApp(context, file);
        }
    }
}
