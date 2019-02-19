package com.owen.tv91;

import android.app.Application;

import com.owen.tv91.utils.ToastUtils;

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
    }

    public static App get() {
        return instance;
    }
}
