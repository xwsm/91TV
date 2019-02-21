package com.owen.tv91.adapter;

import android.content.Context;

import com.owen.adapter.CommonRecyclerViewAdapter;
import com.owen.adapter.CommonRecyclerViewHolder;
import com.owen.tv91.R;
import com.owen.tv91.bean.Channel;
import com.owen.tv91.bean.SearchWithChannel;
import com.owen.tv91.bean.Type;

import java.util.List;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/1/18
 */
public class SearchChannelsAdapter extends CommonRecyclerViewAdapter<SearchWithChannel> {

    public SearchChannelsAdapter(Context context, List<SearchWithChannel> datas) {
        super(context, datas);
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.item_type_list;
    }

    @Override
    public void onBindItemHolder(CommonRecyclerViewHolder helper, SearchWithChannel item, int position) {
        helper.getHolder().setText(R.id.item_type_list_text, R.string.search_channel_text, item.channel, item.movies.size());
    }
}
