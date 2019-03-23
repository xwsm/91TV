package com.owen.tv91;

import android.app.Application;

import com.owen.tv91.utils.ToastUtils;
import com.owen.tvrecyclerview.utils.Loger;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import org.litepal.LitePal;

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

        // 友盟：初始化SDK
        UMConfigure.init(this, "5c77a8b00cafb212e3000383", "github", UMConfigure.DEVICE_TYPE_BOX, null);
        // 友盟：选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);

        // x5：初始化SDK
        QbSdk.initX5Environment(this, null);

        // 数据库初始化
        LitePal.initialize(this);

        Loger.isDebug = true;
    }

    public static App get() {
        return instance;
    }
}
