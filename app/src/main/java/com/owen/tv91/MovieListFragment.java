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

import com.owen.data.resource.DataResParser;
import com.owen.data.resource.MovieDetailBean;
import com.owen.focus.FocusBorder;
import com.owen.tvrecyclerview.widget.SimpleOnItemListener;
import com.owen.tvrecyclerview.widget.TvRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/1/18
 */
public class MovieListFragment extends Fragment {

    public static Fragment newInstance(String url) {
        Fragment fragment = new MovieListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @BindView(R.id.fragment_movie_list_rv)
    TvRecyclerView mRecyclerView;
    @BindView(R.id.fragment_movie_list_progress_bar)
    ProgressBar mProgressBar;

    private View mRootView;
    private FocusBorder mFocusBorder;
    private Unbinder mUnbinder;
    private Disposable mDisposable;
    private String mUrl;
    private List<MovieDetailBean> mMovieDetailBeans;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(getArguments() != null) {
            mUrl = getArguments().getString("url");
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
                    mFocusBorder.onFocus(itemView, FocusBorder.OptionsFactory.get(1.1f, 1.1f));
                }

                @Override
                public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                    DetailActivity.sMovieDetailBean = mMovieDetailBeans.get(position);
                    startActivity(new Intent(getContext(), DetailActivity.class));
                }
            });
            mRecyclerView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    mFocusBorder.setVisible(hasFocus);
                }
            });
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        if(null == mMovieDetailBeans && !TextUtils.isEmpty(mUrl)) {
            mProgressBar.setVisibility(View.VISIBLE);

            if(null != mDisposable && !mDisposable.isDisposed()) {
                mDisposable.dispose();
            }
            mDisposable = Observable.create(new ObservableOnSubscribe<List<MovieDetailBean>>() {
                @Override
                public void subscribe(ObservableEmitter<List<MovieDetailBean>> observableEmitter) throws Exception {
                    observableEmitter.onNext(DataResParser.getMovies(mUrl));
                    observableEmitter.onComplete();
                }
            }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<MovieDetailBean>>() {
                    @Override
                    public void accept(List<MovieDetailBean> movieDetailBeans) throws Exception {
                        Log.i("zsq", "成功");
                        mProgressBar.setVisibility(View.GONE);
                        mMovieDetailBeans = movieDetailBeans;
                        mRecyclerView.setAdapter(new MovieListAdapter(getContext(), movieDetailBeans));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
        }
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
