package com.owen.tv91;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ProgressBar;

import com.owen.base.frame.MvpBaseFragment;
import com.owen.base.frame.MvpBaseView;
import com.owen.focus.FocusBorder;
import com.owen.tv91.adapter.MovieListAdapter;
import com.owen.tv91.adapter.MovieTypeMenuAdapter;
import com.owen.tv91.bean.Channel;
import com.owen.tv91.bean.Movie;
import com.owen.tv91.bean.MoviesResult;
import com.owen.tv91.presenter.MovieListPresenter;
import com.owen.tv91.utils.DisplayUtils;
import com.owen.tv91.utils.FocusBroderHelper;
import com.owen.tvrecyclerview.widget.SimpleOnItemListener;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.owen.tvrecyclerview.widget.V7GridLayoutManager;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/1/18
 */
public class MovieListFragment extends MvpBaseFragment<MovieListPresenter, MvpBaseView> {

    public static Fragment newInstance(Channel channel) {
        Fragment fragment = new MovieListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("channel", channel);
        fragment.setArguments(bundle);
        return fragment;
    }

    @BindView(R.id.fragment_movie_list_menu_rv)
    TvRecyclerView mMenuRecyclerView;
    @BindView(R.id.fragment_movie_list_rv)
    TvRecyclerView mRecyclerView;
    @BindView(R.id.fragment_movie_list_progress_bar)
    ProgressBar mProgressBar;

    private FocusBorder mFocusBorder;
    private Unbinder mUnbinder;
    private Channel mChannel;
    private MovieListAdapter mMovieListAdapter;

    private static final int MSG_TYPE_SELECTED = 111;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TYPE_SELECTED:
                    mPresenter.onTypeSelected(msg.arg1);
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(mChannel.channel);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mChannel.channel);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mUnbinder.unbind();
        mHandler.removeMessages(MSG_TYPE_SELECTED);
        mHandler = null;
    }

    @Override
    protected void initBundleExtra(Bundle savedInstanceState) {
        if(getArguments() != null) {
            mChannel = getArguments().getParcelable("channel");
            mPresenter.setChannel(mChannel);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_movie_list;
    }

    @NonNull
    @Override
    protected MovieListPresenter onCreatePresenter() {
        return new MovieListPresenter();
    }

    @Override
    protected void initViews(@NonNull View view) {
        mUnbinder = ButterKnife.bind(this, view);
        mFocusBorder = FocusBroderHelper.create(view.findViewById(R.id.fragment_list_content_layout));

        mRecyclerView.setLoadMoreBeforehandCount(10);

        if(null != mChannel) {
            if(mChannel.hasTypes()) {
                mMenuRecyclerView.setAdapter(new MovieTypeMenuAdapter(getContext(), mChannel.types));
            } else {
                mMenuRecyclerView.setVisibility(View.GONE);
                mRecyclerView.setPadding((int) DisplayUtils.dp2Px(60), mRecyclerView.getPaddingTop(), mRecyclerView.getPaddingRight(), mRecyclerView.getPaddingBottom());
                if(null != mRecyclerView.getLayoutManager()) {
                    ((V7GridLayoutManager) mRecyclerView.getLayoutManager()).setSpanCount(6);
                }
            }
        }
    }

    @Override
    protected void initListeners() {
        mRecyclerView.setOnItemListener(new SimpleOnItemListener() {
            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                mFocusBorder.onFocus(itemView, FocusBorder.OptionsFactory.get(1.1f, 1.1f));
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("id", mPresenter.getMovie(position).id);
                startActivity(intent);
            }
        });
        mRecyclerView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mFocusBorder.setVisible(hasFocus);
            }
        });
        mRecyclerView.setOnLoadMoreListener(new TvRecyclerView.OnLoadMoreListener() {
            @Override
            public boolean onLoadMore() {
                mRecyclerView.setLoadingMore(true);
                mPresenter.onLoadMore();
                return false;
            }
        });

        mMenuRecyclerView.setOnItemListener(new SimpleOnItemListener() {
            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                mHandler.removeMessages(MSG_TYPE_SELECTED);
                Message msg = mHandler.obtainMessage();
                msg.arg1 = position;
                msg.what = MSG_TYPE_SELECTED;
                mHandler.sendMessageDelayed(msg, 1500);
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                mMenuRecyclerView.setItemActivated(position);
                mPresenter.onTypeSelected(position);
            }
        });
    }

    @Override
    public void onPresenterEvent(int code, @Nullable Bundle bundle) {
        switch (code) {
            case MovieListPresenter.CODE_LOADING:
                mMovieListAdapter = null;
                mRecyclerView.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                mPresenter.clearMovies();
                break;

            case MovieListPresenter.CODE_LOAD_SUCCESS:
                if(null != bundle) {
                    MoviesResult moviesResult = bundle.getParcelable(MovieListPresenter.KEY_MOVIES_RESULT);
                    if(null != moviesResult) {
                        int posStart = mPresenter.sizeMovies();
                        if(null == mMovieListAdapter) {
                            mProgressBar.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            mMovieListAdapter = new MovieListAdapter(getContext(), mPresenter.getMovies());
                            mRecyclerView.setAdapter(mMovieListAdapter, true);
                        }
                        mMovieListAdapter.notifyItemRangeInserted(posStart, moviesResult.content.size());
                        mRecyclerView.setLoadingMore(false);
                        mRecyclerView.setHasMoreData(!moviesResult.isLast());
                    }
                }
                break;
        }
    }
}
