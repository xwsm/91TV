package com.owen.tv91;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.owen.focus.FocusBorder;
import com.owen.tv91.adapter.MovieListAdapter;
import com.owen.tv91.adapter.SearchChannelsAdapter;
import com.owen.tv91.bean.Movie;
import com.owen.tv91.bean.SearchWithChannel;
import com.owen.tv91.network.NetWorkManager;
import com.owen.tv91.network.response.ResponseTransformer;
import com.owen.tv91.network.schedulers.SchedulerProvider;
import com.owen.tv91.utils.FocusBroderHelper;
import com.owen.tv91.utils.ToastUtils;
import com.owen.tvgridlayout.TvGridLayout;
import com.owen.tvrecyclerview.widget.SimpleOnItemListener;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/1/18
 */
public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.activity_search_et)
    EditText mSearchEt;
    @BindView(R.id.activity_search_channel_list)
    TvRecyclerView mChannelRecyclerView;
    @BindView(R.id.activity_search_list)
    TvRecyclerView mRecyclerView;
    @BindView(R.id.activity_search_progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.activity_search_keyboard_tvgl)
    TvGridLayout mTvGridLayout;

    private FocusBorder mFocusBorder;
    private List<SearchWithChannel> mChannels;
    private List<Movie> mMovies = new ArrayList<>();
    private Disposable mSearchDisposable;
    private int mSelectedChannelPosition = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        mFocusBorder = FocusBroderHelper.create(this);

        mChannelRecyclerView.setOnItemListener(new SimpleOnItemListener() {
            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                parent.setItemActivated(position);
                onItemSelected(parent, itemView, position);
            }

            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                if(mSelectedChannelPosition != position) {
                    onSelectedChannel(position);
                }
            }
        });

        mRecyclerView.setOnItemListener(new SimpleOnItemListener() {

            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                mFocusBorder.onFocus(itemView, FocusBorder.OptionsFactory.get(1.1f, 1.1f));
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra("id", mMovies.get(position).id);
                startActivity(intent);
            }
        });
        mRecyclerView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mFocusBorder.setVisible(hasFocus, hasFocus);
            }
        });

        mTvGridLayout.setOnItemListener(new com.owen.tvgridlayout.SimpleOnItemListener() {

            @Override
            public void onItemSelected(TvGridLayout parent, View itemView, int position) {
                mFocusBorder.onFocus(itemView, FocusBorder.OptionsFactory.get(1.1f, 1.1f));
            }

            @Override
            public void onItemClick(TvGridLayout parent, View itemView, int position) {
                if(position == 0) {
                    mSearchEt.setText("");
                } else if(position == 1) {
                    String old = mSearchEt.getText().toString();
                    if(!TextUtils.isEmpty(old)) {
                        char[] chars = old.toCharArray();
                        mSearchEt.setText(chars, 0,chars.length - 1);
                    }
                } else {
                    String old = mSearchEt.getText().toString();
                    mSearchEt.setText(old + ((TextView)itemView).getText().toString());
                }
                onSearch();
            }
        });
        mTvGridLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mFocusBorder.setVisible(hasFocus, hasFocus);
            }
        });
    }

    private void onSelectedChannel(int position) {
        mSelectedChannelPosition = position;
        mMovies = mChannels.get(mSelectedChannelPosition).movies;
        mRecyclerView.setAdapter(new MovieListAdapter(SearchActivity.this, mMovies), true);
    }

    private void onSearch() {
        mRecyclerView.setVisibility(View.GONE);
        mChannelRecyclerView.setVisibility(View.GONE);
        final String word = mSearchEt.getText().toString();
        if(!TextUtils.isEmpty(word)) {
            if(null != mSearchDisposable && !mSearchDisposable.isDisposed()) {
                mSearchDisposable.dispose();
            }
            mProgressBar.setVisibility(View.VISIBLE);
            mSearchDisposable = NetWorkManager.getRequest().searchWithChannel(word)
                    .compose(SchedulerProvider.getInstance().applySchedulers())
                    .compose(ResponseTransformer.handleResult())
                    .subscribe(new Consumer<List<SearchWithChannel>>() {
                        @Override
                        public void accept(List<SearchWithChannel> channels) throws Exception {
                            mProgressBar.setVisibility(View.GONE);
                            if(null != channels && !channels.isEmpty()) {
                                mRecyclerView.setVisibility(View.VISIBLE);
                                mChannelRecyclerView.setVisibility(View.VISIBLE);
                                mChannels = channels;
                                mChannelRecyclerView.setAdapter(new SearchChannelsAdapter(SearchActivity.this, mChannels), true);
                                onSelectedChannel(0);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            ToastUtils.showShortToast("搜索失败！");
                        }
                    });

        } else {
            if(null != mRecyclerView.getAdapter()) {
                mMovies.clear();
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != mSearchDisposable) {
            mSearchDisposable.dispose();
        }
    }
}
