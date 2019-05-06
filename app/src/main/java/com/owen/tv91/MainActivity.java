package com.owen.tv91;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.owen.base.frame.MvpBaseActivity;
import com.owen.base.frame.MvpBaseView;
import com.owen.tab.TvTabLayout;
import com.owen.tv91.adapter.MainPagerAdapter;
import com.owen.tv91.bean.AppUpdate;
import com.owen.tv91.bean.Channel;
import com.owen.tv91.network.NetWorkManager;
import com.owen.tv91.network.response.ResponseTransformer;
import com.owen.tv91.network.schedulers.SchedulerProvider;
import com.owen.tv91.presenter.MainPresenter;
import com.owen.tv91.utils.GlideApp;
import com.owen.tv91.utils.ToastUtils;
import com.owen.tv91.widget.UpdateDialog;
import com.owen.widget.TvViewPager;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.youngkaaa.yviewpager.YViewPager;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends MvpBaseActivity<MainPresenter, MvpBaseView> {

    @BindView(R.id.activity_main_bg_iv)
    ImageView mBgImgView;
    @BindView(R.id.search_button)
    Button mSearchBtn;
    @BindView(R.id.history_button)
    Button mHistoryBtn;
    @BindView(R.id.tablayout1)
    TvTabLayout mTabLayout;
    @BindView(R.id.activity_main_view_pager)
    TvViewPager mViewPager;
    @BindView(R.id.activity_main_progress_bar)
    ProgressBar mProgressBar;

    private UpdateDialog mUpdateDialog;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i("zsq", "onConfigurationChanged...");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("zsq", "onDestroy...");

        if(null != mUpdateDialog && mUpdateDialog.isShowing()) {
            mUpdateDialog.cancel();
        }
    }

    @NonNull
    @Override
    protected MainPresenter onCreatePresenter() {
        return new MainPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        ButterKnife.bind(this);

        GlideApp.with(this).load(R.drawable.bg).into(mBgImgView);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setScrollerDuration(200);
    }

    @Override
    protected void initListeners() {
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });
        mSearchBtn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                v.animate().scaleX(hasFocus ? 1.1f : 1f).scaleY(hasFocus ? 1.1f : 1f).setDuration(300).start();
            }
        });

        mHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
            }
        });
        mHistoryBtn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                v.animate().scaleX(hasFocus ? 1.1f : 1f).scaleY(hasFocus ? 1.1f : 1f).setDuration(300).start();
            }
        });

        mTabLayout.requestFocus();
    }

    @Override
    public void onPresenterEvent(int code, @Nullable Bundle bundle) {
        switch (code) {
            case MainPresenter.CODE_LOADING:
                mProgressBar.setVisibility(View.VISIBLE);
                break;

            case MainPresenter.CODE_LOAD_SUCCESS:
                if (null != bundle) {
                    List<Channel> channels = bundle.getParcelableArrayList(MainPresenter.KEY_CHANNELS);
                    mProgressBar.setVisibility(View.GONE);
                    mViewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager(), channels));
                }
                break;

            case MainPresenter.CODE_CHECK_UPDATE:
                if (null != bundle) {
                    AppUpdate appUpdate = bundle.getParcelable(MainPresenter.KEY_APP_UPDATE);
                    if(null !=appUpdate && appUpdate.versionCode > BuildConfig.VERSION_CODE) {
                        mUpdateDialog = new UpdateDialog(MainActivity.this, appUpdate);
                        mUpdateDialog.show();
                    }
                }
                break;
        }
    }
}
