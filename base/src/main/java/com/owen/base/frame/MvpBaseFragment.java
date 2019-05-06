package com.owen.base.frame;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 *
 * @author ZhouSuQiang
 * @date 2018/10/9
 */
public abstract class MvpBaseFragment<P extends MvpBaseFragmentPresenter<V>, V extends MvpBaseView> extends Fragment implements MvpBaseView{

    protected P mPresenter;

    private boolean mInitialized;
    protected View mRootView;
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    
        mPresenter = onCreatePresenter();
        mPresenter.onPresenterView(getPresenterView());
        mPresenter.onAttach(context);
        getLifecycle().addObserver(mPresenter);
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mRootView) {
            mRootView = inflater.inflate(getLayoutId(), container, false);
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }

        mPresenter.onCreateView();

        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (!mInitialized) {
            initBundleExtra(savedInstanceState);
            initViews(view);
            initListeners();
            if (getUserVisibleHint()) {
                onLazyLoad();
            }
            mInitialized = true;
        }

        mPresenter.onViewCreated();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter.onActivityCreated();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.onDestroyView();
    }
    
    @Override
    public void onDetach() {
        super.onDetach();
        mPresenter.onDetach();
        getLifecycle().removeObserver(mPresenter);
        mPresenter = null;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (mInitialized) {
            if (getUserVisibleHint()) {
                onLazyLoad();
            }
        }
    }

    /**
     * Override in case of fragment not implementing MvpBasePresenter<View> interface
     */
    @NonNull
    protected V getPresenterView() {
        return (V) this;
    }

    @LayoutRes
    protected abstract int getLayoutId();

    @NonNull
    protected abstract P onCreatePresenter();

    /**
     * 懒加载回调
     */
    protected void onLazyLoad() {
    }

    /**
     * 1.获取get Intent数据 2.状态保存数据读取
     */
    protected void initBundleExtra(Bundle savedInstanceState) {
    }

    /**
     * 初始化UI view
     */
    protected abstract void initViews(@NonNull View view);

    /**
     * 初始化view监听
     */
    protected void initListeners() {}
}
