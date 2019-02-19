package com.owen.tv91;

import android.app.Application;

import com.owen.tv91.utils.ToastUtils;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/2/18
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ToastUtils.initToast(this);
    }
}
