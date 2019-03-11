package com.owen.tv91;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.owen.focus.FocusBorder;
import com.owen.tv91.adapter.HistoryMovieListAdapter;
import com.owen.tv91.dao.HistoryMovie;
import com.owen.tv91.utils.FocusBroderHelper;
import com.owen.tvrecyclerview.widget.SimpleOnItemListener;
import com.owen.tvrecyclerview.widget.TvRecyclerView;

import org.litepal.LitePal;
import org.litepal.crud.callback.FindMultiCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/3/11
 */
public class HistoryActivity extends AppCompatActivity {
    @BindView(R.id.activity_history_list_rv)
    TvRecyclerView mRecyclerView;
    @BindView(R.id.activity_history_list_progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.activity_history_empty_hint_tv)
    TextView mEmptyHintView;

    private List<HistoryMovie> mMovies;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_list);
        ButterKnife.bind(this);

        FocusBorder focusBorder = FocusBroderHelper.create(this);
        focusBorder.boundGlobalFocusListener(new FocusBorder.OnFocusCallback() {
            @Nullable
            @Override
            public FocusBorder.Options onFocus(View oldFocus, View newFocus) {
                return FocusBorder.OptionsFactory.get(1.1f, 1.1f);
            }
        });

        mRecyclerView.setOnItemListener(new SimpleOnItemListener() {
            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra("id", mMovies.get(position).movieId);
                startActivity(intent);
            }
        });

        LitePal.order("updateDateMillis desc")
                .findAsync(HistoryMovie.class).listen(new FindMultiCallback<HistoryMovie>() {
            @Override
            public void onFinish(List<HistoryMovie> list) {
                mProgressBar.setVisibility(View.GONE);
                mMovies = list;
                if(null != mMovies && !mMovies.isEmpty()) {
                    mEmptyHintView.setVisibility(View.GONE);
                    mRecyclerView.setAdapter(new HistoryMovieListAdapter(getApplicationContext(), mMovies));
                } else {
                    mEmptyHintView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
