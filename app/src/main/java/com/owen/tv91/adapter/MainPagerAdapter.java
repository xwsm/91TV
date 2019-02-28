package com.owen.tv91.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.owen.tv91.MovieListFragment;
import com.owen.tv91.bean.Channel;

import java.util.List;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/2/28
 */
public class MainPagerAdapter extends FragmentPagerAdapter {
    private List<Channel> mNavs;

    public MainPagerAdapter(FragmentManager fm, List<Channel> navs) {
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
