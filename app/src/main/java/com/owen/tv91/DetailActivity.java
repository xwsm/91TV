package com.owen.tv91;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.owen.adapter.CommonRecyclerViewAdapter;
import com.owen.adapter.CommonRecyclerViewHolder;
import com.owen.player.PlayerSettings;
import com.owen.player.bean.MediaBean;
import com.owen.tv91.bean.MovieDetail;
import com.owen.tv91.bean.PlaySource;
import com.owen.tv91.bean.PlayUrl;
import com.owen.tv91.network.NetWorkManager;
import com.owen.tv91.network.response.ResponseTransformer;
import com.owen.tv91.network.schedulers.SchedulerProvider;
import com.owen.tv91.utils.ToastUtils;
import com.owen.tvrecyclerview.widget.SimpleOnItemListener;
import com.owen.tvrecyclerview.widget.TvRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
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

    @BindViews({R.id.activity_detail_play_list_title1_tv, R.id.activity_detail_play_list_title2_tv, R.id.activity_detail_play_list_title3_tv})
    TextView[] mPlayTitleTvs;
    @BindViews({R.id.activity_detail_play_list1_rv, R.id.activity_detail_play_list2_rv, R.id.activity_detail_play_list3_rv})
    TvRecyclerView[] mPlayListRvs;
    
    private MovieDetail mMovieDetail;
    private Disposable mDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        long id = getIntent().getLongExtra("id", 0);


        mDisposable = NetWorkManager.getRequest().detail(id)
                .compose(SchedulerProvider.getInstance().applySchedulers())
                .compose(ResponseTransformer.handleResult())
                .subscribe(new Consumer<MovieDetail>() {
                    @Override
                    public void accept(MovieDetail movieDetail) throws Exception {
                        mMovieDetail = movieDetail;
                        setData();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtils.showShortToast("获取影片详情数据失败！");
                    }
                });

    }

    private void setData() {
        if(null != mMovieDetail) {
            Glide.with(this).load(mMovieDetail.img).into(mPosterIv);
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
                int size = mMovieDetail.playSources.size() > mPlayTitleTvs.length ? mPlayTitleTvs.length : mMovieDetail.playSources.size();
                for(int i=0; i<size; i++) {
                    final PlaySource playSource = mMovieDetail.playSources.get(i);
                    mPlayTitleTvs[i].setText("来源：" + playSource.name);
                    mPlayListRvs[i].setAdapter(new PlayUrlAdapter(this, playSource.playUrls));

                    mPlayListRvs[i].setOnItemListener(new SimpleOnItemListener() {
                        @Override
                        public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                            List<MediaBean> data = new ArrayList<>();
                            MediaBean bean;
                            for(PlayUrl playUrl : playSource.playUrls) {
                                bean = new MediaBean();
                                bean.id = mMovieDetail.id + "";
                                bean.name = mMovieDetail.name;
                                bean.playUrl = playUrl.playUrl;
                                bean.playName = playUrl.name;
                                data.add(bean);
                            }
                            PlayerSettings.getInstance(getApplicationContext())
                                    .setPlayerType(PlayerSettings.PLAYER_TYPE_ANDROID)
                                    .setMediaList(data)
                                    .setPlayIndex(position)
                                    .startPlayer(DetailActivity.this);
                        }
                    });
                }

                // 初始化焦点
                mPlayListRvs[0].post(new Runnable() {
                    @Override
                    public void run() {
                        mPlayListRvs[0].requestDefaultFocus();
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != mDisposable && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
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
