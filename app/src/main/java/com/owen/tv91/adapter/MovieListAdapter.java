package com.owen.tv91.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.owen.adapter.CommonRecyclerViewAdapter;
import com.owen.adapter.CommonRecyclerViewHolder;
import com.owen.tv91.R;
import com.owen.tv91.bean.Movie;

import java.util.List;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/1/18
 */
public class MovieListAdapter extends CommonRecyclerViewAdapter<Movie> {

    public MovieListAdapter(Context context, List<Movie> datas) {
        super(context, datas);
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.item_movie;
    }

    @Override
    public void onBindItemHolder(CommonRecyclerViewHolder helper, Movie item, int position) {
        helper.getHolder().setText(R.id.item_name_tv, item.name)
                .setText(R.id.item_sketch_tv, item.sketch)
                .setText(R.id.item_score_tv, item.score)
                .setVisibility(R.id.item_score_tv, (TextUtils.isEmpty(item.score) || TextUtils.equals(item.score, "0.0")) ? View.INVISIBLE : View.VISIBLE);
        ImageView imageView = helper.getHolder().getView(R.id.item_poster_iv);
        Glide.with(mContext).load(item.img).placeholder(R.drawable.icon_img_default).into(imageView);
    }
}
