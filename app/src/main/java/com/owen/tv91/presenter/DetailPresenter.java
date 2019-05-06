package com.owen.tv91.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.owen.base.frame.MvpBasePresenter;
import com.owen.base.frame.MvpBaseView;
import com.owen.player.PlayProgressListener;
import com.owen.player.PlayerSettings;
import com.owen.player.bean.MediaBean;
import com.owen.player.bean.ParamBean;
import com.owen.tv91.DetailActivity;
import com.owen.tv91.adapter.MovieListAdapter;
import com.owen.tv91.bean.Movie;
import com.owen.tv91.bean.MovieDetail;
import com.owen.tv91.bean.PlaySource;
import com.owen.tv91.bean.PlayUrl;
import com.owen.tv91.dao.HistoryMovie;
import com.owen.tv91.network.NetWorkManager;
import com.owen.tv91.network.response.ResponseTransformer;
import com.owen.tv91.network.schedulers.SchedulerProvider;
import com.owen.tv91.utils.ToastUtils;
import com.tencent.smtt.sdk.TbsVideo;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-05-05
 */
public class DetailPresenter extends MvpBasePresenter<MvpBaseView> implements PlayProgressListener {
    public static final int CODE_LOAD_SIMILAR_SUCCESS = 101;
    public static final int CODE_UPDATE_HISTORY = 102;
    public static final String KEY_MOVIE_DETAIL = "key_movie_detail";
    public static final String KEY_SIMILAR_MOVIES = "key_similar_movies";

    private List<Disposable> mDisposables = new ArrayList<>();
    private long movieId;
    private MovieDetail mMovieDetail;
    private HistoryMovie mHistoryMovie;
    private int mHistoryPlayIndex;
    private int mPlaySourcesPosition;

    public DetailPresenter (long movieId) {
        this.movieId = movieId;
    }

    public HistoryMovie getHistoryMovie() {
        return mHistoryMovie;
    }

    @Override
    public void onCreate() {
        loadHistory();
        loadDatas();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        for(Disposable disposable : mDisposables) {
            if (disposable.isDisposed()) {
                disposable.dispose();
            }
        }
    }

    public void playMovie(Activity contenxt, int playSourcesPosition, int playUrlsPosition) {
        if(null == mMovieDetail || !mMovieDetail.hasPlaySources()) {
            ToastUtils.showShortToast("播放地址出错！");
        }

        mPlaySourcesPosition = playSourcesPosition;

        PlaySource playSource = mMovieDetail.playSources.get(playSourcesPosition);
        List<MediaBean> data = new ArrayList<>();
        MediaBean bean;
        for (PlayUrl playUrl : playSource.playUrls) {
            bean = new MediaBean();
            bean.id = mMovieDetail.id + "";
            bean.name = mMovieDetail.name;
            bean.playUrl = playUrl.playUrl.replace("http:", "https:");
            bean.playName = playUrl.name;
            data.add(bean);
        }

        int time = (null != mHistoryMovie && mHistoryMovie.playUrlId
                == playSource.playUrls.get(playUrlsPosition).id) ? mHistoryMovie.playTime : 0;
        PlayerSettings.getInstance(contenxt)
                .setPlayerType(PlayerSettings.PLAYER_TYPE_EXO)
                .setUsingHardwareDecoder(true)
                .setMediaList(data)
                .setPlayIndex(playUrlsPosition)
                .setPlayTime(time)
                .setPlayProgressListener(this)
                .startPlayer(contenxt);

        // 腾讯X5播放器
        /*if(TbsVideo.canUseTbsPlayer(contenxt)) {
            Bundle extraData = new Bundle();
            extraData.putString("title", mMovieDetail.name);
            extraData.putInt("screenMode", 102); //来实现默认全屏+控制栏等UI
            TbsVideo.openVideo(contenxt, playSource.playUrls.get(position).playUrl, extraData);
        }*/
    }

    public void setHistoryPlayIndex(int historyPlayIndex) {
        mHistoryPlayIndex = historyPlayIndex;
    }

    public int getHistoryPlayIndex() {
        return mHistoryPlayIndex;
    }

    // 历史数据
    private void loadHistory() {
        List<HistoryMovie> historyMovies = LitePal.where("movieId = ?", "" + movieId).find(HistoryMovie.class);
        if(null != historyMovies && !historyMovies.isEmpty()) {
            mHistoryMovie = historyMovies.get(0);
        }
    }

    private void loadDatas() {
        Disposable disposable = NetWorkManager.getRequest().detail(movieId)
                .compose(SchedulerProvider.getInstance().applySchedulers())
                .compose(ResponseTransformer.handleResult())
                .subscribe(new Consumer<MovieDetail>() {
                    @Override
                    public void accept(MovieDetail movieDetail) throws Exception {
                        mMovieDetail = movieDetail;
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(KEY_MOVIE_DETAIL, movieDetail);
                        onPresenterEvent(CODE_LOAD_SUCCESS, bundle);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        ToastUtils.showShortToast("获取影片详情数据失败！");

                        onPresenterEvent(CODE_LOAD_FAILURE, null);
                    }
                });
        mDisposables.add(disposable);

        disposable = NetWorkManager.getRequest().similarMovies(movieId, 12)
                .compose(SchedulerProvider.getInstance().applySchedulers())
                .compose(ResponseTransformer.handleResult())
                .subscribe(new Consumer<List<Movie>>() {
                    @Override
                    public void accept(List<Movie> movies) throws Exception {
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList(KEY_SIMILAR_MOVIES, new ArrayList<>(movies));
                        onPresenterEvent(CODE_LOAD_SIMILAR_SUCCESS, bundle);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        ToastUtils.showShortToast("获取相关推荐影片失败！");
                    }
                });
        mDisposables.add(disposable);
    }

    @Override
    public void onPlayProgress(@Nullable Intent data) {
        if(null != data) {
            int index = data.getIntExtra(ParamBean.KEY_PLAY_INDEX, 0);
            int time = data.getIntExtra(ParamBean.KEY_PLAYED_TIME, 0);
            int duration = data.getIntExtra(ParamBean.KEY_DURATION_TIME, 0);
//            MediaBean mediaBean = data.getParcelableExtra(ParamBean.KEY_MEDIA_OBJECT);

            Log.i("zzssqq", "time="+time+" duration="+duration);

            final HistoryMovie historyMovie = null == mHistoryMovie ? new HistoryMovie() : mHistoryMovie;
            historyMovie.movieId = mMovieDetail.id;
            historyMovie.movieName = mMovieDetail.name;
            historyMovie.movieAlias = mMovieDetail.alias;
            historyMovie.movieChannel = mMovieDetail.channel;
            historyMovie.movieImg = mMovieDetail.img;
            historyMovie.movieScore = mMovieDetail.score;
            historyMovie.movieShowDate = mMovieDetail.showDate;
            historyMovie.movieSketch = mMovieDetail.sketch;
            historyMovie.movieType = mMovieDetail.type;
            PlaySource playSource = mMovieDetail.playSources.get(mPlaySourcesPosition);
            historyMovie.playSourceId = playSource.id;
            historyMovie.playSourceName = playSource.name;
            historyMovie.playTime = time;
            historyMovie.playDuration = duration;
            historyMovie.playUrlId = playSource.playUrls.get(index).id;
            historyMovie.playUrlName = playSource.playUrls.get(index).name;
            historyMovie.updateDateMillis = System.currentTimeMillis();
            historyMovie.saveOrUpdate("movieId = ?", "" + mMovieDetail.id);

            mHistoryMovie = historyMovie;
            mHistoryPlayIndex = index;

            onPresenterEvent(CODE_UPDATE_HISTORY, null);
        }
    }
}
