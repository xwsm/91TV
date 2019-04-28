package com.owen.tv91;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.owen.tab.TvTabLayout;
import com.owen.tv91.adapter.MainPagerAdapter;
import com.owen.tv91.bean.AppUpdate;
import com.owen.tv91.bean.Channel;
import com.owen.tv91.network.NetWorkManager;
import com.owen.tv91.network.response.ResponseTransformer;
import com.owen.tv91.network.schedulers.SchedulerProvider;
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

public class MainActivity extends AppCompatActivity {

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

    private Disposable mDisposable;
    private Disposable mDisposable1;
    private UpdateDialog mUpdateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();
        loadChannelDatas();
        checkUpdate();
    }

    private void initView() {
        GlideApp.with(this).load(R.drawable.bg).into(mBgImgView);

        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setScrollerDuration(200);

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

    private void loadChannelDatas() {
        mDisposable = NetWorkManager.getRequest().channels()
                .compose(SchedulerProvider.getInstance().applySchedulers())
                .compose(ResponseTransformer.handleResult())
                .subscribe(new Consumer<List<Channel>>() {
                    @Override
                    public void accept(List<Channel> channels) throws Exception {
                        mProgressBar.setVisibility(View.GONE);
                        mViewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager(), channels));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtils.showShortToast("加载导航失败！");
                    }
                });
    }

    private void checkUpdate() {
        mDisposable1 = NetWorkManager.getRequest().update()
                .compose(SchedulerProvider.getInstance().applySchedulers())
                .compose(ResponseTransformer.handleResult())
                .subscribe(new Consumer<AppUpdate>() {
                    @Override
                    public void accept(AppUpdate appUpdate) throws Exception {
                        if(appUpdate.versionCode > BuildConfig.VERSION_CODE) {
                            mUpdateDialog = new UpdateDialog(MainActivity.this, appUpdate);
                            mUpdateDialog.show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtils.showShortToast("检查版本更新失败！");
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != mDisposable) {
            mDisposable.dispose();
        }
        if(null != mDisposable1) {
            mDisposable1.dispose();
        }
        if(null != mUpdateDialog && mUpdateDialog.isShowing()) {
            mUpdateDialog.cancel();
        }
    }

}
