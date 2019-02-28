package com.owen.tv91;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.owen.focus.FocusBorder;
import com.owen.tv91.adapter.MovieListAdapter;
import com.owen.tv91.adapter.MovieTypeMenuAdapter;
import com.owen.tv91.bean.Channel;
import com.owen.tv91.bean.Movie;
import com.owen.tv91.bean.MoviesResult;
import com.owen.tv91.network.NetWorkManager;
import com.owen.tv91.network.response.ResponseTransformer;
import com.owen.tv91.network.schedulers.SchedulerProvider;
import com.owen.tv91.utils.DisplayUtils;
import com.owen.tv91.utils.FocusBroderHelper;
import com.owen.tv91.utils.ToastUtils;
import com.owen.tvrecyclerview.widget.SimpleOnItemListener;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.owen.tvrecyclerview.widget.V7GridLayoutManager;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/1/18
 */
public class MovieListFragment extends Fragment {

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

    private View mRootView;
    private FocusBorder mFocusBorder;
    private Unbinder mUnbinder;
    private Disposable mDisposable;
    private Channel mChannel;
    private MovieListAdapter mMovieListAdapter;
    private List<Movie> mMovies = new ArrayList<>();
    private int mPageIndex = 0;
    private int mPageSize = 50;
    private int mCurTypePosition = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(getArguments() != null) {
            mChannel = getArguments().getParcelable("channel");
        }
    }

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(null == mRootView) {
            mRootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
            mUnbinder = ButterKnife.bind(this, mRootView);
            mFocusBorder = FocusBroderHelper.create(mRootView.findViewById(R.id.fragment_list_content_layout));

            mRecyclerView.setLoadMoreBeforehandCount(10);
            mRecyclerView.setOnItemListener(new SimpleOnItemListener() {
                @Override
                public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                    mFocusBorder.onFocus(itemView, FocusBorder.OptionsFactory.get(1.1f, 1.1f));
                }

                @Override
                public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                    Intent intent = new Intent(getContext(), DetailActivity.class);
                    intent.putExtra("id", mMovies.get(position).id);
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
                    mPageIndex ++;
                    loadDatas();
                    return false;
                }
            });

            mMenuRecyclerView.setOnItemListener(new SimpleOnItemListener() {

                @Override
                public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                    if(mCurTypePosition != position) {
                        mCurTypePosition = position;
                        mPageIndex = 0;
                        loadDatas();
                    }
                }

                @Override
                public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                    mMenuRecyclerView.setItemActivated(position);
                    onItemSelected(parent, itemView, position);
                }
            });
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        if(mMovies.isEmpty() && null != mChannel) {
            if(mChannel.hasTypes()) {
                mMenuRecyclerView.setAdapter(new MovieTypeMenuAdapter(getContext(), mChannel.types));
            } else {
                mMenuRecyclerView.setVisibility(View.GONE);
                mRecyclerView.setPadding((int) DisplayUtils.dp2Px(60), mRecyclerView.getPaddingTop(), mRecyclerView.getPaddingRight(), mRecyclerView.getPaddingBottom());
                if(null != mRecyclerView.getLayoutManager()) {
                    ((V7GridLayoutManager) mRecyclerView.getLayoutManager()).setSpanCount(6);
                }
            }
            loadDatas();
        }
    }

    private String getCurType() {
        if(null != mChannel && mChannel.hasTypes()) {
            String type = mChannel.types.get(mCurTypePosition).type;
            if(TextUtils.equals("全部", type)) {
                return  "";
            }
            return type;
        }
        return "";
    }

    private void loadDatas() {
        if (mPageIndex == 0) {
            mRecyclerView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            mMovies.clear();
        }
        if(null != mDisposable && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        mDisposable = NetWorkManager.getRequest().moviesByChannelAndType(mChannel.channel, getCurType(), mPageIndex, mPageSize)
                .compose(SchedulerProvider.getInstance().applySchedulers())
                .compose(ResponseTransformer.handleResult())
                .subscribe(new Consumer<MoviesResult>() {
                    @Override
                    public void accept(MoviesResult moviesResult) throws Exception {
                        int posStart = mMovies.size();
                        mMovies.addAll(moviesResult.content);
                        if(mPageIndex == 0) {
                            mProgressBar.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            mMovieListAdapter = new MovieListAdapter(getContext(), mMovies);
                            mRecyclerView.setAdapter(mMovieListAdapter, true);
                        }
                        mMovieListAdapter.notifyItemRangeInserted(posStart, moviesResult.content.size());
                        mRecyclerView.setLoadingMore(false);
                        mRecyclerView.setHasMoreData(!moviesResult.isLast());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtils.showShortToast("加载列表数据失败！");
                    }
                });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mUnbinder.unbind();
        if(null != mDisposable) {
            mDisposable.dispose();
        }
    }

}
