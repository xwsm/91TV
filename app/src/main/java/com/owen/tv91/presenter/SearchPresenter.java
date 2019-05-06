package com.owen.tv91.presenter;

import android.os.Bundle;
import android.view.View;

import com.owen.base.frame.MvpBasePresenter;
import com.owen.base.frame.MvpBaseView;
import com.owen.tv91.SearchActivity;
import com.owen.tv91.adapter.MovieListAdapter;
import com.owen.tv91.adapter.SearchChannelsAdapter;
import com.owen.tv91.bean.SearchWithChannel;
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
public class SearchPresenter extends MvpBasePresenter<MvpBaseView> {
    public static final String KEY_SEARCH_WITH_CHANNEL = "key_search_with_channel";

    private Disposable mSearchDisposable;

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null != mSearchDisposable) {
            mSearchDisposable.dispose();
        }
    }

    public void requestSearch(String word) {
        if(null != mSearchDisposable && !mSearchDisposable.isDisposed()) {
            mSearchDisposable.dispose();
        }
        mSearchDisposable = NetWorkManager.getRequest().searchWithChannel(word)
                .compose(SchedulerProvider.getInstance().applySchedulers())
                .compose(ResponseTransformer.handleResult())
                .subscribe(new Consumer<List<SearchWithChannel>>() {
                    @Override
                    public void accept(List<SearchWithChannel> channels) throws Exception {
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList(KEY_SEARCH_WITH_CHANNEL, new ArrayList<>(channels));
                        onPresenterEvent(CODE_LOAD_SUCCESS, bundle);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtils.showShortToast("搜索失败！");
                        onPresenterEvent(CODE_LOAD_FAILURE, null);
                    }
                });
    }

}
