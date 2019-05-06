package com.owen.base.frame;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

/**
 *
 * @author ZhouSuQiang
 * @date 2018/10/9
 */
public abstract class MvpBaseActivity<P extends MvpBasePresenter<V>, V extends MvpBaseView> extends AppCompatActivity implements MvpBaseView {

    private static final String TAG = MvpBaseActivity.class.getSimpleName();
    protected P mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutId());
        initBundleExtra(savedInstanceState);
        initViews();
        initListeners();

        mPresenter = onCreatePresenter();
        mPresenter.onPresenterView(getPresenterView());
        getLifecycle().addObserver(mPresenter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getLifecycle().removeObserver(mPresenter);
        mPresenter = null;
    }

    @NonNull
    protected V getPresenterView() {
        return (V) this;
    }

    @NonNull
    protected abstract P onCreatePresenter();
    
    @LayoutRes
    protected abstract int getLayoutId();

    /**
     * 初始化UI view
     */
    protected abstract void initViews();

    /**
     * 1.获取get Intent数据 2.状态保存数据读取
     */
    protected void initBundleExtra(Bundle savedInstanceState) {
    }

    /**
     * 初始化view监听
     */
    protected void initListeners() {}
}
