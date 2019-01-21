package com.owen.tv91;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.owen.data.resource.DataResParser;
import com.owen.data.resource.MovieDetailBean;
import com.owen.data.resource.NavigationBean;
import com.owen.tab.TvTabLayout;
import com.owen.widget.TvViewPager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.search_button)
    Button mSearchBtn;
    @BindView(R.id.tablayout1)
    TvTabLayout mTabLayout;
    @BindView(R.id.activity_main_view_pager)
    TvViewPager mViewPager;

    private List<NavigationBean> mNavigationList;
    private List<MovieDetailBean> mMovieDetailBeans;

    private Disposable mMovieListSubscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setScrollerDuration(400);

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

        Disposable disposable = Observable.create(new ObservableOnSubscribe<List<NavigationBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<NavigationBean>> observableEmitter) throws Exception {
                observableEmitter.onNext(DataResParser.getNavigations());
                observableEmitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<NavigationBean>>() {
                    @Override
                    public void accept(List<NavigationBean> navigationBeans) throws Exception {
                        mNavigationList = navigationBeans;
                        Log.i("zsq", "导航 成功" + mNavigationList);
//                        for (NavigationBean nav : navigationBeans) {
//                            mTabLayout.addTab(mTabLayout.newTab().setText(nav.name));
//                        }

                        mViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), navigationBeans));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i("zsq", "导航 失败..." + throwable);
                    }
                });
    }

    class PagerAdapter extends FragmentPagerAdapter {
        private List<NavigationBean> mNavs;

        public PagerAdapter(FragmentManager fm, List<NavigationBean> navs) {
            super(fm);
            mNavs = navs;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mNavs.get(position).name;
        }

        @Override
        public Fragment getItem(int i) {
            return MovieListFragment.newInstance(mNavs.get(i).url);
        }

        @Override
        public int getCount() {
            return null != mNavs ? mNavs.size() : 0;
        }
    }
}
