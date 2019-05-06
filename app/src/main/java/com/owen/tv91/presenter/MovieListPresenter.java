package com.owen.tv91.presenter;

import android.os.Bundle;
import android.text.TextUtils;

import com.owen.base.frame.MvpBaseFragmentPresenter;
import com.owen.base.frame.MvpBaseView;
import com.owen.tv91.bean.Channel;
import com.owen.tv91.bean.Movie;
import com.owen.tv91.bean.MoviesResult;
import com.owen.tv91.network.NetWorkManager;
import com.owen.tv91.network.response.ResponseTransformer;
import com.owen.tv91.network.schedulers.SchedulerProvider;
import com.owen.tv91.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-05-05
 */
public class MovieListPresenter extends MvpBaseFragmentPresenter<MvpBaseView> {
    public static final String KEY_MOVIES_RESULT = "KEY_MOVIES_RESULT";

    private Disposable mDisposable;
    private List<Movie> mMovies = new ArrayList<>();
    private Channel mChannel;
    private int mCurTypePosition = 0;
    private int mPageIndex = 0;
    private static int sPageSize = 50;

    @Override
    public void onCreate() {

    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        if (mMovies.isEmpty()) {
            loadMovieList();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null != mDisposable) {
            mDisposable.dispose();
        }
    }

    public List<Movie> getMovies() {
        return mMovies;
    }

    public void clearMovies() {
        mMovies.clear();
    }

    public int sizeMovies() {
        return mMovies.size();
    }

    public Movie getMovie(int position) {
        return mMovies.get(position);
    }

    public void setChannel(Channel channel) {
        mChannel = channel;
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

    public void onTypeSelected(int position) {
        if(mCurTypePosition != position) {
            mCurTypePosition = position;
            mPageIndex = 0;
            loadMovieList();
        }
    }

    public void onLoadMore() {
        mPageIndex ++;
        loadMovieList();
    }

    public void loadMovieList() {
        if(mPageIndex == 0) {
            onPresenterEvent(CODE_LOADING, null);
        }

        if(null != mDisposable && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        mDisposable = NetWorkManager.getRequest().moviesByChannelAndType(mChannel.channel, getCurType(), mPageIndex, sPageSize)
                .compose(SchedulerProvider.getInstance().applySchedulers())
                .compose(ResponseTransformer.handleResult())
                .subscribe(new Consumer<MoviesResult>() {
                    @Override
                    public void accept(MoviesResult moviesResult) throws Exception {
                        mMovies.addAll(moviesResult.content);

                        Bundle bundle = new Bundle();
                        bundle.putParcelable(KEY_MOVIES_RESULT, moviesResult);
                        onPresenterEvent(CODE_LOAD_SUCCESS, bundle);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mPageIndex --;
                        ToastUtils.showShortToast("加载列表数据失败！");
                        onPresenterEvent(CODE_LOAD_FAILURE, null);
                    }
                });
    }
}
