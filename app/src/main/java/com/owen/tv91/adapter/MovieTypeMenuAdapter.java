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
import com.owen.tv91.bean.Type;

import java.util.List;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/1/18
 */
public class MovieTypeMenuAdapter extends CommonRecyclerViewAdapter<Type> {

    public MovieTypeMenuAdapter(Context context, List<Type> datas) {
        super(context, datas);
    }

    @Override
    public void setDatas(List<Type> datas) {
        Type allType = new Type();
        allType.type = "全部";
        datas.add(0, allType);
        super.setDatas(datas);
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.item_type_list;
    }

    @Override
    public void onBindItemHolder(CommonRecyclerViewHolder helper, Type item, int position) {
        helper.getHolder().setText(R.id.item_type_list_text, item.type);
    }
}
