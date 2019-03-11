package com.owen.tv91;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.owen.adapter.CommonRecyclerViewAdapter;
import com.owen.adapter.CommonRecyclerViewHolder;
import com.owen.player.PlayerSettings;
import com.owen.player.bean.MediaBean;
import com.owen.player.bean.ParamBean;
import com.owen.tab.TvTabLayout;
import com.owen.tv91.bean.MovieDetail;
import com.owen.tv91.bean.PlaySource;
import com.owen.tv91.bean.PlayUrl;
import com.owen.tv91.dao.HistoryMovie;
import com.owen.tv91.network.NetWorkManager;
import com.owen.tv91.network.response.ResponseTransformer;
import com.owen.tv91.network.schedulers.SchedulerProvider;
import com.owen.tv91.utils.GlideApp;
import com.owen.tv91.utils.ToastUtils;
import com.owen.tvrecyclerview.widget.SimpleOnItemListener;
import com.owen.tvrecyclerview.widget.TvRecyclerView;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/1/17
 */
public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.activity_detail_poster_iv)
    public ImageView mPosterIv;
    @BindView(R.id.activity_detail_name_tv)
    public TextView mNameTv; //姓名，无双
    @BindView(R.id.activity_detail_sketch_tv)
    public TextView mSketchTv; //简述，HD1280高清国语中字版/更新至20190114
    @BindView(R.id.activity_detail_score_tv)
    public TextView mScoreTv; //评分，7.9
    @BindView(R.id.activity_detail_alias_tv)
    public TextView mAliasTv; //英文名/别名，Project Gutenberg,Mo seung
    @BindView(R.id.activity_detail_director_tv)
    public TextView mDirectorTv; //导演，庄文强
    @BindView(R.id.activity_detail_starring_tv)
    public TextView mStarringTv; //主演，周润发,郭富城,张静初,冯文娟
    @BindView(R.id.activity_detail_type_tv)
    public TextView mTypeTv; //类型，动作片
    @BindView(R.id.activity_detail_area_tv)
    public TextView mAreaTv; //地区，大陆
    @BindView(R.id.activity_detail_language_tv)
    public TextView mLanguageTv; //语言，国语
    @BindView(R.id.activity_detail_show_date_tv)
    public TextView mShowDateTv; //上映日期，2018
    @BindView(R.id.activity_detail_duration_tv)
    public TextView mDurationTv; //片长，130
    @BindView(R.id.activity_detail_update_date_tv)
    public TextView mUpdateDateTv; //更新日期，2018-12-21
    @BindView(R.id.activity_detail_intro_tv)
    public TextView mIntroTv; //简介
    @BindView(R.id.activity_detail_progress_bar)
    public ProgressBar mProgressBar;
    @BindView(R.id.activity_detail_play_source_tablayout)
    TvTabLayout mTabLayout;
    @BindView(R.id.activity_detail_play_list1_rv)
    TvRecyclerView mPlayListRv;
    @BindView(R.id.activity_detail_play_btn)
    Button mPlayButton;
    
    private MovieDetail mMovieDetail;
    private Disposable mDisposable;
    private HistoryMovie mHistoryMovie;
    private int mHistoryPlayIndex;
    private int mHistoryPlaySourceIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        long id = getIntent().getLongExtra("id", 0);

        List<HistoryMovie> historyMovies = LitePal.where("movieId = ?", "" + id).find(HistoryMovie.class);
        if(null != historyMovies && !historyMovies.isEmpty()) {
            mHistoryMovie = historyMovies.get(0);
        }

        mDisposable = NetWorkManager.getRequest().detail(id)
                .compose(SchedulerProvider.getInstance().applySchedulers())
                .compose(ResponseTransformer.handleResult())
                .subscribe(new Consumer<MovieDetail>() {
                    @Override
                    public void accept(MovieDetail movieDetail) throws Exception {
                        mProgressBar.setVisibility(View.GONE);
                        mMovieDetail = movieDetail;
                        setData();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        mProgressBar.setVisibility(View.GONE);
                        ToastUtils.showShortToast("获取影片详情数据失败！");
                    }
                });

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != mMovieDetail && mMovieDetail.hasPlaySources()) {
                    playMovie(mMovieDetail.playSources.get(mHistoryPlaySourceIndex), mHistoryPlayIndex);

//                    if(TbsVideo.canUseTbsPlayer(getApplicationContext())) {
//                        Bundle extraData = new Bundle();
//                        extraData.putString("title", mMovieDetail.name);
//                        extraData.putInt("screenMode", 102); //来实现默认全屏+控制栏等UI
//                        TbsVideo.openVideo(getApplicationContext(),
//                                mMovieDetail.playSources.get(0).playUrls.get(0).playUrl, extraData);
//                    }

                } else {
                    ToastUtils.showShortToast("播放地址出错！");
                }
            }
        });
    }

    private void setData() {
        if(null != mMovieDetail) {
            GlideApp.with(this).load(mMovieDetail.img).placeholder(R.drawable.icon_img_default).into(mPosterIv);
            mScoreTv.setVisibility(TextUtils.isEmpty(mMovieDetail.score) || TextUtils.equals(mMovieDetail.score, "0.0")
                            ? View.INVISIBLE : View.VISIBLE);
            mScoreTv.setText(mMovieDetail.score);
            mNameTv.setText(mMovieDetail.name);
            mSketchTv.setText(mMovieDetail.sketch);
            mAliasTv.setText("别名：" + mMovieDetail.alias);
            mDirectorTv.setText("导演：" + mMovieDetail.director);
            mStarringTv.setText("主演：" + mMovieDetail.starring);
            mTypeTv.setText("类型：" + mMovieDetail.type);
            mAreaTv.setText("区域：" + mMovieDetail.area);
            mLanguageTv.setText("语言：" + mMovieDetail.language);
            mShowDateTv.setText("上映：" + mMovieDetail.showDate);
            mDurationTv.setText("时长：" + mMovieDetail.duration);
            mUpdateDateTv.setText("更新：" + mMovieDetail.updateDate);
            mIntroTv.setText("剧情简介：" + mMovieDetail.intro);

            if(mMovieDetail.hasPlaySources()) {
                if(null != mHistoryMovie) {
                    mPlayButton.setText(String.format("续播 %s", mHistoryMovie.playUrlName));
                } else if(mMovieDetail.hasPlaySources() && mMovieDetail.playSources.get(0).hasPlayUrls()){
                    mPlayButton.setText(String.format("播放 %s", mMovieDetail.playSources.get(0).playUrls.get(0).name));
                }

                mTabLayout.addOnTabSelectedListener(new TvTabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TvTabLayout.Tab tab) {
                        mHistoryPlaySourceIndex = tab.getPosition();
                        final PlaySource playSource = mMovieDetail.playSources.get(tab.getPosition());

                        mPlayListRv.setAdapter(new PlayUrlAdapter(DetailActivity.this, playSource.playUrls), true);

                        mPlayListRv.setOnItemListener(new SimpleOnItemListener() {
                            @Override
                            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                                itemView.animate().scaleX(1.1f).scaleY(1.1f).setDuration(300).start();
                            }

                            @Override
                            public void onItemPreSelected(TvRecyclerView parent, View itemView, int position) {
                                itemView.animate().scaleX(1f).scaleY(1f).setDuration(300).start();
                            }

                            @Override
                            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                                playMovie(playSource, position);
                            }
                        });
                    }

                    @Override
                    public void onTabUnselected(TvTabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TvTabLayout.Tab tab) {

                    }
                });

                mTabLayout.removeAllTabs();
                int i = 0;
                Iterator<PlaySource> iterator = mMovieDetail.playSources.iterator();
                while (iterator.hasNext()) {
                    PlaySource playSource = iterator.next();
                    if(playSource.hasPlayUrls()) {
                        boolean selected = null == mHistoryMovie ? (i == 0) : (mHistoryMovie.playSourceId == playSource.id);
                        mTabLayout.addTab(mTabLayout.newTab().setText(playSource.name), selected);
                        if(null != mHistoryMovie && selected) {
                            mHistoryPlaySourceIndex = i;
                        }
                        i++;
                    } else {
                        iterator.remove();
                    }
                }

                if(null != mHistoryMovie) {
                    int j = 0;
                    for (PlayUrl playUrl : mMovieDetail.playSources.get(mHistoryPlaySourceIndex).playUrls) {
                        if (playUrl.id == mHistoryMovie.playUrlId) {
                            mHistoryPlayIndex = j;
                            break;
                        }
                        j++;
                    }
                }
            }
        }
    }

    private void playMovie(PlaySource playSource, int position) {
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
                == playSource.playUrls.get(position).id) ? mHistoryMovie.playTime : 0;
        PlayerSettings.getInstance(getApplicationContext())
                .setPlayerType(PlayerSettings.PLAYER_TYPE_EXO)
                .setUsingHardwareDecoder(true)
                .setMediaList(data)
                .setPlayIndex(position)
                .setPlayTime(time)
                .startPlayer(DetailActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != mDisposable && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(null != data && resultCode == PlayerSettings.RESULT_CODE) {
            int index = data.getIntExtra(ParamBean.KEY_PLAY_INDEX, 0);
            int time = data.getIntExtra(ParamBean.KEY_PLAYED_TIME, 0);
            int duration = data.getIntExtra(ParamBean.KEY_DURATION_TIME, 0);
//            MediaBean mediaBean = data.getParcelableExtra(ParamBean.KEY_MEDIA_OBJECT);

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
            PlaySource playSource = mMovieDetail.playSources.get(mTabLayout.getSelectedTabPosition());
            historyMovie.playSourceId = playSource.id;
            historyMovie.playSourceName = playSource.name;
            historyMovie.playTime = time;
            historyMovie.playDuration = duration;
            historyMovie.playUrlId = playSource.playUrls.get(index).id;
            historyMovie.playUrlName = playSource.playUrls.get(index).name;
            historyMovie.updateDateMillis = System.currentTimeMillis();
            historyMovie.saveOrUpdate("movieId = ?", "" + mMovieDetail.id);

            mHistoryMovie = historyMovie;
            mPlayButton.setText(String.format("续播 %s", mHistoryMovie.playUrlName));
            mHistoryPlayIndex = index;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class PlayUrlAdapter extends CommonRecyclerViewAdapter<PlayUrl> {

        public PlayUrlAdapter(Context context, List<PlayUrl> datas) {
            super(context, datas);
        }

        @Override
        public int getItemLayoutId(int viewType) {
            return R.layout.item_detail_play_list;
        }

        @Override
        public void onBindItemHolder(CommonRecyclerViewHolder helper, PlayUrl item, int position) {
            helper.getHolder().setText(R.id.item_detail_play_list_url_tv, item.name);
        }
    }
}
