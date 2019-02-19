package com.owen.tv91;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.owen.focus.FocusBorder;
import com.owen.tv91.bean.Channel;
import com.owen.tv91.bean.Movie;
import com.owen.tv91.bean.MoviesResult;
import com.owen.tv91.network.NetWorkManager;
import com.owen.tv91.network.response.ResponseTransformer;
import com.owen.tv91.network.schedulers.SchedulerProvider;
import com.owen.tv91.utils.DisplayUtils;
import com.owen.tv91.utils.ToastUtils;
import com.owen.tvrecyclerview.widget.SimpleOnItemListener;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.owen.tvrecyclerview.widget.V7GridLayoutManager;

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
    private List<Movie> mMovies;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(getArguments() != null) {
            mChannel = getArguments().getParcelable("channel");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(null == mRootView) {
            mRootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
            mUnbinder = ButterKnife.bind(this, mRootView);
            mFocusBorder = FocusBroderHelper.create((ViewGroup) mRootView);

            mRecyclerView.setOnItemListener(new SimpleOnItemListener() {
                @Override
                public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                    Log.i("zsq", "position = " + position);
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

            mMenuRecyclerView.setOnItemListener(new SimpleOnItemListener() {
                int curPosition = 0;

                @Override
                public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                    if(curPosition != position) {
                        curPosition = position;
                        loadDatas(mChannel.types.get(position).type);
                    }
                }
            });
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        if(null == mMovies && null != mChannel) {

            if(mChannel.hasTypes()) {
                mMenuRecyclerView.setAdapter(new MovieTypeMenuAdapter(getContext(), mChannel.types));
            } else {
                mMenuRecyclerView.setVisibility(View.GONE);
                mRecyclerView.setPadding((int) DisplayUtils.dp2Px(60), mRecyclerView.getPaddingTop(), mRecyclerView.getPaddingRight(), mRecyclerView.getPaddingBottom());
                if(null != mRecyclerView.getLayoutManager()) {
                    ((V7GridLayoutManager) mRecyclerView.getLayoutManager()).setSpanCount(6);
                }
            }
            loadDatas("");
        }
    }

    private void loadDatas(String type) {
        if(TextUtils.equals("全部", type)) {
            type = "";
        }
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        if(null != mDisposable && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        mDisposable = NetWorkManager.getRequest().moviesByChannelAndType(mChannel.channel, type, 0, 50)
                .compose(SchedulerProvider.getInstance().applySchedulers())
                .compose(ResponseTransformer.handleResult())
                .subscribe(new Consumer<MoviesResult>() {
                    @Override
                    public void accept(MoviesResult moviesResult) throws Exception {
                        mProgressBar.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mMovies = moviesResult.content;
                        mRecyclerView.setAdapter(new MovieListAdapter(getContext(), mMovies), true);
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
