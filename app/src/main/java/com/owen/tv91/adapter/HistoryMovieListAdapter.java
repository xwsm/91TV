package com.owen.tv91.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.owen.adapter.CommonRecyclerViewAdapter;
import com.owen.adapter.CommonRecyclerViewHolder;
import com.owen.tv91.R;
import com.owen.tv91.bean.Movie;
import com.owen.tv91.dao.HistoryMovie;
import com.owen.tv91.utils.GlideApp;

import java.util.List;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/1/18
 */
public class HistoryMovieListAdapter extends CommonRecyclerViewAdapter<HistoryMovie> {

    public HistoryMovieListAdapter(Context context, List<HistoryMovie> datas) {
        super(context, datas);
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.item_movie;
    }

    @Override
    public void onBindItemHolder(CommonRecyclerViewHolder helper, HistoryMovie item, int position) {
        helper.getHolder().setText(R.id.item_name_tv, item.movieName)
                .setText(R.id.item_sketch_tv, item.movieSketch)
                .setText(R.id.item_score_tv, item.movieScore)
                .setVisibility(R.id.item_score_tv, (TextUtils.isEmpty(item.movieScore) || TextUtils.equals(item.movieScore, "0.0")) ? View.INVISIBLE : View.VISIBLE);
        ImageView imageView = helper.getHolder().getView(R.id.item_poster_iv);
        GlideApp.with(mContext).load(item.movieImg).placeholder(R.drawable.icon_img_default).into(imageView);
    }
}
