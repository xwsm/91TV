package com.owen.tv91.presenter;

import android.os.Bundle;

import com.owen.base.frame.MvpBasePresenter;
import com.owen.base.frame.MvpBaseView;
import com.owen.tv91.bean.AppUpdate;
import com.owen.tv91.bean.Channel;
import com.owen.tv91.network.NetWorkManager;
import com.owen.tv91.network.response.ResponseTransformer;
import com.owen.tv91.network.schedulers.SchedulerProvider;
import com.owen.tv91.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-05-05
 */
public class MainPresenter extends MvpBasePresenter<MvpBaseView> {
    public static final int CODE_CHECK_UPDATE = 101;

    public static final String KEY_CHANNELS = "key_channels";
    public static final String KEY_APP_UPDATE = "key_app_update";

    private List<Disposable> mDisposables = new ArrayList<>();

    @Override
    public void onCreate() {
        loadChannelDatas();
        checkUpdate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (Disposable disposable : mDisposables) {
            if(!disposable.isDisposed()) {
                disposable.dispose();
            }
        }
    }

    private void loadChannelDatas() {
        onPresenterEvent(CODE_LOADING, null);

        Disposable disposable = NetWorkManager.getRequest().channels()
                .compose(SchedulerProvider.getInstance().applySchedulers())
                .compose(ResponseTransformer.handleResult())
                .subscribe(new Consumer<List<Channel>>() {
                    @Override
                    public void accept(List<Channel> channels) throws Exception {
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList(KEY_CHANNELS, new ArrayList<>(channels));
                        onPresenterEvent(CODE_LOAD_SUCCESS, bundle);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtils.showShortToast("加载导航失败！");
                        onPresenterEvent(CODE_LOAD_FAILURE, null);
                    }
                });

        mDisposables.add(disposable);
    }

    private void checkUpdate() {
        Disposable disposable = NetWorkManager.getRequest().update()
                .compose(SchedulerProvider.getInstance().applySchedulers())
                .compose(ResponseTransformer.handleResult())
                .subscribe(new Consumer<AppUpdate>() {
                    @Override
                    public void accept(AppUpdate appUpdate) throws Exception {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(KEY_APP_UPDATE, appUpdate);
                        onPresenterEvent(CODE_CHECK_UPDATE, bundle);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtils.showShortToast("检查版本更新失败！");
                    }
                });

        mDisposables.add(disposable);
    }
}
