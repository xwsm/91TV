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

import com.owen.tab.TvTabLayout;
import com.owen.tv91.bean.Channel;
import com.owen.tv91.network.NetWorkManager;
import com.owen.tv91.network.response.ResponseTransformer;
import com.owen.tv91.network.schedulers.SchedulerProvider;
import com.owen.tv91.utils.ToastUtils;
import com.owen.widget.TvViewPager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.search_button)
    Button mSearchBtn;
    @BindView(R.id.tablayout1)
    TvTabLayout mTabLayout;
    @BindView(R.id.activity_main_view_pager)
    TvViewPager mViewPager;

    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setScrollerDuration(200);

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });

        mTabLayout.addOnTabSelectedListener(new TvTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(final TvTabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TvTabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TvTabLayout.Tab tab) {

            }
        });

        mDisposable = NetWorkManager.getRequest().channels()
                .compose(SchedulerProvider.getInstance().applySchedulers())
                .compose(ResponseTransformer.handleResult())
                .subscribe(new Consumer<List<Channel>>() {
                    @Override
                    public void accept(List<Channel> channels) throws Exception {
                        mViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), channels));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtils.showShortToast("加载导航失败！");
                    }
                });

        mTabLayout.requestFocus();
    }

    class PagerAdapter extends FragmentPagerAdapter {
        private List<Channel> mNavs;

        public PagerAdapter(FragmentManager fm, List<Channel> navs) {
            super(fm);
            mNavs = navs;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mNavs.get(position).channel;
        }

        @Override
        public Fragment getItem(int i) {
            return MovieListFragment.newInstance(mNavs.get(i));
        }

        @Override
        public int getCount() {
            return null != mNavs ? mNavs.size() : 0;
        }
    }
}
