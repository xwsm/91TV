package com.owen.tv91;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.owen.adapter.CommonRecyclerViewAdapter;
import com.owen.adapter.CommonRecyclerViewHolder;
import com.owen.base.frame.MvpBaseActivity;
import com.owen.base.frame.MvpBaseView;
import com.owen.tab.TvTabLayout;
import com.owen.tv91.adapter.MovieListAdapter;
import com.owen.tv91.bean.Movie;
import com.owen.tv91.bean.MovieDetail;
import com.owen.tv91.bean.PlaySource;
import com.owen.tv91.bean.PlayUrl;
import com.owen.tv91.dao.HistoryMovie;
import com.owen.tv91.presenter.DetailPresenter;
import com.owen.tv91.utils.GlideApp;
import com.owen.tvrecyclerview.widget.SimpleOnItemListener;
import com.owen.tvrecyclerview.widget.TvRecyclerView;

import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/1/17
 */
public class DetailActivity extends MvpBaseActivity<DetailPresenter, MvpBaseView> {

    @BindView(R.id.activity_detail_content_layout_cl)
    ViewGroup mContentLayout;
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
    @BindView(R.id.activity_detail_bg_iv)
    ImageView mBgIv;
    @BindView(R.id.activity_detail_similar_movies_rv)
    TvRecyclerView mSimilarMoviesRv;

    private long movieId;

    private void setData(MovieDetail mMovieDetail) {
        if(null == mMovieDetail) {
            return;
        }

        HistoryMovie mHistoryMovie = mPresenter.getHistoryMovie();

        GlideApp.with(this).load(mMovieDetail.img)
                .transform(new BlurTransformation(40, 7))
                .into(mBgIv);
        GlideApp.with(this)
                .load(mMovieDetail.img).placeholder(R.drawable.icon_img_default).into(mPosterIv);

        mScoreTv.setVisibility(TextUtils.isEmpty(mMovieDetail.score) || TextUtils.equals(mMovieDetail.score, "0.0")
                        ? View.INVISIBLE : View.VISIBLE);
        mScoreTv.setText(mMovieDetail.score);
        mNameTv.setText(mMovieDetail.name);
        mSketchTv.setText(mMovieDetail.sketch);
        mAliasTv.setText(String.format("别名：%s", mMovieDetail.alias));
        mDirectorTv.setText(String.format("导演：%s", mMovieDetail.director));
        mStarringTv.setText(String.format("主演：%s", mMovieDetail.starring));
        mTypeTv.setText(String.format("类型：%s", TextUtils.isEmpty(mMovieDetail.types) ? mMovieDetail.type : mMovieDetail.types));
        mAreaTv.setText(String.format("区域：%s", mMovieDetail.area));
        mLanguageTv.setText(String.format("语言：%s", mMovieDetail.language));
        mShowDateTv.setText(String.format("上映：%s", mMovieDetail.showDate));
        mDurationTv.setText(String.format("时长：%s", mMovieDetail.duration));
        mUpdateDateTv.setText(String.format("更新：%s", mMovieDetail.updateDate));
        mIntroTv.setText(String.format("剧情简介：%s", mMovieDetail.intro));

        if(mMovieDetail.hasPlaySources()) {
            if(null != mHistoryMovie) {
                mPlayButton.setText(String.format("续播 %s", mHistoryMovie.playUrlName));
            } else if(mMovieDetail.hasPlaySources() && mMovieDetail.playSources.get(0).hasPlayUrls()){
                mPlayButton.setText(String.format("播放 %s", mMovieDetail.playSources.get(0).playUrls.get(0).name));
            }

            mTabLayout.addOnTabSelectedListener(new TvTabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TvTabLayout.Tab tab) {
                    final PlaySource playSource = mMovieDetail.playSources.get(tab.getPosition());
                    mPlayListRv.setAdapter(new PlayUrlAdapter(DetailActivity.this, playSource.playUrls), true);
                }

                @Override
                public void onTabUnselected(TvTabLayout.Tab tab) { }

                @Override
                public void onTabReselected(TvTabLayout.Tab tab) { }
            });

            mTabLayout.removeAllTabs();
            int historyPlaySourceIndex = 0;
            int i = 0;
            Iterator<PlaySource> iterator = mMovieDetail.playSources.iterator();
            while (iterator.hasNext()) {
                PlaySource playSource = iterator.next();
                if(playSource.hasPlayUrls()) {
                    mTabLayout.addTab(mTabLayout.newTab().setText(playSource.name.toUpperCase()));
                    if(null != mHistoryMovie && mHistoryMovie.playSourceId == playSource.id) {
                        historyPlaySourceIndex = i;
                    }
                    i++;
                } else {
                    iterator.remove();
                }
            }
            // 选中指定tab
            final int playSourceIndex = historyPlaySourceIndex;
            mTabLayout.post(new Runnable() {
                @Override
                public void run() {
                    mTabLayout.selectTab(playSourceIndex);
                }
            });

            if(null != mHistoryMovie) {
                int j = 0;
                for (PlayUrl playUrl : mMovieDetail.playSources.get(historyPlaySourceIndex).playUrls) {
                    if (playUrl.id == mHistoryMovie.playUrlId) {
                        mPresenter.setHistoryPlayIndex(j);
                        break;
                    }
                    j++;
                }
            }
        }

        // 初始化焦点
        mPlayButton.post(new Runnable() {
            @Override
            public void run() {
                mPlayButton.requestFocus();
                mPlayButton.requestFocusFromTouch();
            }
        });
    }

    @NonNull
    @Override
    protected DetailPresenter onCreatePresenter() {
        return new DetailPresenter(movieId);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_detail;
    }

    @Override
    protected void initBundleExtra(Bundle savedInstanceState) {
        movieId = getIntent().getLongExtra("id", 0);
    }

    @Override
    protected void initViews() {
        ButterKnife.bind(this);

        mContentLayout.setVisibility(View.INVISIBLE);
        mSimilarMoviesRv.setFocusable(false);
        mSimilarMoviesRv.setFocusableInTouchMode(false);
        mPlayListRv.setFocusable(false);
        mPlayListRv.setFocusableInTouchMode(false);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.playMovie(DetailActivity.this, mTabLayout.getSelectedTabPosition(), mPresenter.getHistoryPlayIndex());
            }
        });

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
                mPresenter.playMovie(DetailActivity.this, mTabLayout.getSelectedTabPosition(), position);
            }
        });
    }

    @Override
    public void onPresenterEvent(int code, @Nullable Bundle bundle) {
        switch (code) {
            case DetailPresenter.CODE_LOAD_SUCCESS:
                mProgressBar.setVisibility(View.GONE);
                mContentLayout.setVisibility(View.VISIBLE);
                if (null != bundle) {
                    MovieDetail movieDetail = bundle.getParcelable(DetailPresenter.KEY_MOVIE_DETAIL);
                    setData(movieDetail);
                }
                break;

            case DetailPresenter.CODE_LOAD_FAILURE:
                mProgressBar.setVisibility(View.GONE);
                mContentLayout.setVisibility(View.VISIBLE);
                break;

            case DetailPresenter.CODE_LOAD_SIMILAR_SUCCESS:
                if(null != bundle) {
                    List<Movie> movies = bundle.getParcelableArrayList(DetailPresenter.KEY_SIMILAR_MOVIES);
                    mSimilarMoviesRv.setAdapter(new MovieListAdapter(getApplicationContext(), movies));
                    mSimilarMoviesRv.setOnItemListener(new SimpleOnItemListener() {
                        @Override
                        public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                            Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                            intent.putExtra("id", movies.get(position).id);
                            startActivity(intent);
                        }

                        @Override
                        public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                            itemView.animate().scaleX(1.1f).scaleY(1.1f).setDuration(300).start();
                        }

                        @Override
                        public void onItemPreSelected(TvRecyclerView parent, View itemView, int position) {
                            itemView.animate().scaleX(1f).scaleY(1f).setDuration(300).start();
                        }
                    });
                }
                break;

            case DetailPresenter.CODE_UPDATE_HISTORY:
                mPlayButton.setText(String.format("续播 %s", mPresenter.getHistoryMovie().playUrlName));
                break;
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
