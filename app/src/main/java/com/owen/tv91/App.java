package com.owen.tv91;

import android.app.Application;

import com.owen.tv91.utils.ToastUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/2/18
 */
public class App extends Application {
    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        ToastUtils.initToast(this);

        // 初始化SDK
        UMConfigure.init(this, "5c77a8b00cafb212e3000383", "github", UMConfigure.DEVICE_TYPE_PHONE, null);
        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
    }

    public static App get() {
        return instance;
    }
}
