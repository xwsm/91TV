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
import com.owen.data.resource.MovieDetailBean;
import com.owen.tvrecyclerview.widget.SimpleOnItemListener;
import com.owen.tvrecyclerview.widget.TvRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/1/17
 */
public class DetailActivity extends AppCompatActivity {
    public static MovieDetailBean sMovieDetailBean;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
//        String detailUrl = getIntent().getStringExtra("detailUrl");

        setData();
    }

    private void setData() {
        if(null != sMovieDetailBean) {
            Glide.with(this).load(sMovieDetailBean.img).into(mPosterIv);
            mNameTv.setText(sMovieDetailBean.name);
            mSketchTv.setText(sMovieDetailBean.sketch);
            mAliasTv.setText(sMovieDetailBean.alias);
            mDirectorTv.setText(sMovieDetailBean.director);
            mStarringTv.setText(sMovieDetailBean.starring);
            mTypeTv.setText(sMovieDetailBean.type);
            mAreaTv.setText(sMovieDetailBean.area);
            mLanguageTv.setText(sMovieDetailBean.language);
            mShowDateTv.setText(sMovieDetailBean.showDate);
            mDurationTv.setText(sMovieDetailBean.duration);
            mUpdateDateTv.setText(sMovieDetailBean.updateDate);
            mIntroTv.setText("剧情简介：" + sMovieDetailBean.intro);

            if(sMovieDetailBean.hasPlaySources()) {
                int size = sMovieDetailBean.playSources.size() > mPlayTitleTvs.length ? mPlayTitleTvs.length : sMovieDetailBean.playSources.size();
                for(int i=0; i<size; i++) {
                    final MovieDetailBean.PlaySource playSource = sMovieDetailBean.playSources.get(i);
                    mPlayTitleTvs[i].setText("来源：" + playSource.name);
                    mPlayListRvs[i].setAdapter(new PlayUrlAdapter(this, playSource.urls));

                    mPlayListRvs[i].setOnItemListener(new SimpleOnItemListener() {
                        @Override
                        public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                            String url = playSource.urls.get(position).url;
                            String extension = MimeTypeMap.getFileExtensionFromUrl(url);
                            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
//                            String mimeType = "video/*";
                            Intent mediaIntent = new Intent(Intent.ACTION_VIEW);
                            mediaIntent.setDataAndType(Uri.parse(url), mimeType);
                            startActivity(mediaIntent);
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
        sMovieDetailBean = null;
    }


    class PlayUrlAdapter extends CommonRecyclerViewAdapter<MovieDetailBean.PlayUrl> {

        public PlayUrlAdapter(Context context, List<MovieDetailBean.PlayUrl> datas) {
            super(context, datas);
        }

        @Override
        public int getItemLayoutId(int viewType) {
            return R.layout.item_detail_play_list;
        }

        @Override
        public void onBindItemHolder(CommonRecyclerViewHolder helper, MovieDetailBean.PlayUrl item, int position) {
            helper.getHolder().setText(R.id.item_detail_play_list_url_tv, item.name);
        }
    }
}
