package com.owen.player.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.owen.player.PlayerSettings;
import com.owen.player.R;
import com.owen.player.bean.MediaBean;
import com.owen.tvrecyclerview.widget.TvRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by owen on 16/5/10.
 */
public class PlayListPopupWindow extends PopupWindow {
    private static final String TAG = PlayListPopupWindow.class.getSimpleName() + " %s";

    private Context mContext;
    private LayoutInflater mInflater;
    private TvRecyclerView mRecyclerView;
    private TextView mMediaNameView;
    private List<MediaBean> mMedias = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;
    private View oldView;
    private int mPlayPosition;

    public PlayListPopupWindow(Context context, boolean focusable) {
        mContext = context;
        setContentView(initView());
        setBackgroundDrawable(new BitmapDrawable());
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(focusable);
//        setAnimationStyle(R.style.EduTvPlayer_bottom_anim_style);
    }

    private View initView() {
        mInflater = LayoutInflater.from(mContext);
        View rootView = mInflater.inflate(R.layout.edu_tvplayer_play_list_layout, null);
        mMediaNameView = (TextView) rootView.findViewById(R.id.tv_media_name);
        mRecyclerView = (TvRecyclerView) rootView.findViewById(R.id.gd_play_list);
        initListener();
        return rootView;
    }

    private void initListener() {
        mRecyclerView.setOnItemListener(new TvRecyclerView.OnItemListener() {
            @Override
            public void onItemPreSelected(TvRecyclerView parent, View itemView, int position) {

            }

            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                if (null != mMedias && !mMedias.isEmpty() && position >= 0 && position < mMedias.size()) {
                    MediaBean mediaBean = mMedias.get(position);
                    mMediaNameView.setText((position + 1) + " : " + mediaBean.name);
                }
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                mPlayPosition = PlayerSettings.getInstance(mContext).getPlayIndex();
                if (null != mOnItemClickListener && position != mPlayPosition) {
                    setItemActivated(itemView);
                    mOnItemClickListener.onItemClick(parent, itemView, position);
                }
            }
        });

    }

    private void setItemActivated(View itemView) {
        if (null == itemView) return;
        View currPlayView;
        if (null != oldView) {
            oldView.setActivated(false);
            currPlayView = oldView.findViewById(R.id.tv_play_list_item_label_curr_play);
            if (null != currPlayView) {
                currPlayView.setVisibility(View.GONE);
            }
        }
        oldView = itemView;
        oldView.setActivated(true);
        currPlayView = oldView.findViewById(R.id.tv_play_list_item_label_curr_play);
        if (null != currPlayView) {
            currPlayView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        if (null != mRecyclerView) {
            mPlayPosition = PlayerSettings.getInstance(mContext).getPlayIndex();
            mRecyclerView.setSelection(mPlayPosition);
            mRecyclerView.setItemActivated(mPlayPosition);
        }
        super.showAtLocation(parent, gravity, x, y);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setPlaylist(List<MediaBean> list) {
        if (null == list) return;
        mMedias = list;
        mRecyclerView.setAdapter(new PlayListAdapter());
    }

    public interface OnItemClickListener {
        void onItemClick(ViewGroup parent, View itemView, int position);
    }

    private class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHold> {
        @Override
        public ViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(mContext).inflate(R.layout.edu_tvplayer_play_list_item, parent, false);
            return new ViewHold(view);
        }

        @Override
        public void onBindViewHolder(ViewHold holder, int position) {
            holder.mTextView.setText(String.valueOf(position + 1));
            holder.itemView.setActivated(false);
            holder.mCruuPlayView.setVisibility(View.GONE);
            holder.mFreeView.setVisibility(View.GONE);
            if (position == mPlayPosition) {
                oldView = holder.itemView;
                oldView.setActivated(true);
                holder.mCruuPlayView.setVisibility(View.VISIBLE);
            }

            if (null != mMedias && !mMedias.isEmpty()) {
                MediaBean mediaBean = mMedias.get(position);
                holder.mTextView.setText(mediaBean.playName);

                // 判断free标签是否显示
//                boolean isFree = null != mediaBean && TextUtils.equals("0", mediaBean.isBuy);
//                if (isFree) {
//                    holder.mFreeView.setVisibility(View.VISIBLE);
//                }
            }
        }

        @Override
        public int getItemCount() {
            return mMedias.size();
        }

        public class ViewHold extends RecyclerView.ViewHolder {

            public TextView mTextView;
            public ImageView mFreeView;
            public ImageView mCruuPlayView;

            public ViewHold(View itemView) {
                super(itemView);
                mTextView = (TextView) itemView.findViewById(R.id.tv_play_list_item_position);
                mFreeView = (ImageView) itemView.findViewById(R.id.tv_play_list_item_label_free);
                mCruuPlayView = (ImageView) itemView.findViewById(R.id.tv_play_list_item_label_curr_play);
            }
        }
    }
}
