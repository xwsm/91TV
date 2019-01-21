package com.owen.tv91;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.owen.data.resource.DataResParser;
import com.owen.data.resource.MovieDetailBean;
import com.owen.focus.AbsFocusBorder;
import com.owen.focus.FocusBorder;
import com.owen.tvrecyclerview.widget.SimpleOnItemListener;
import com.owen.tvrecyclerview.widget.TvRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/1/18
 */
public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.activity_search_button)
    Button mSearchBtn;
    @BindView(R.id.activity_search_et)
    EditText mSearchEt;
    @BindView(R.id.activity_search_list)
    TvRecyclerView mRecyclerView;
    @BindView(R.id.activity_search_progress_bar)
    ProgressBar mProgressBar;

    private FocusBorder mFocusBorder;
    private List<MovieDetailBean> mMovieDetailBeans;
    private Disposable mSearchDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        mFocusBorder = FocusBroderHelper.create(this);

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String word = mSearchEt.getText().toString();
                if(!TextUtils.isEmpty(word)) {
                    if(null != mSearchDisposable && !mSearchDisposable.isDisposed()) {
                        mSearchDisposable.dispose();
                    }
                    mProgressBar.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                    mSearchDisposable = Observable.create(new ObservableOnSubscribe<List<MovieDetailBean>>() {
                        @Override
                        public void subscribe(ObservableEmitter<List<MovieDetailBean>> observableEmitter) throws Exception {
                            observableEmitter.onNext(DataResParser.search(word));
                            observableEmitter.onComplete();
                        }
                    }).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<List<MovieDetailBean>>() {
                                @Override
                                public void accept(List<MovieDetailBean> movieDetailBeans) throws Exception {
                                    Log.i("zsq", "成功");
                                    mProgressBar.setVisibility(View.GONE);
                                    mRecyclerView.setVisibility(View.VISIBLE);
                                    mMovieDetailBeans = movieDetailBeans;
                                    mRecyclerView.setAdapter(new MovieListAdapter(SearchActivity.this, movieDetailBeans));
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    mProgressBar.setVisibility(View.GONE);
                                    mRecyclerView.setVisibility(View.VISIBLE);
                                }
                            });
                }
            }
        });


        mRecyclerView.setOnItemListener(new SimpleOnItemListener() {
            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                DetailActivity.sMovieDetailBean = mMovieDetailBeans.get(position);
                startActivity(new Intent(SearchActivity.this, DetailActivity.class));
            }
        });

        mFocusBorder.boundGlobalFocusListener(new FocusBorder.OnFocusCallback() {
            @Nullable
            @Override
            public FocusBorder.Options onFocus(View oldFocus, View newFocus) {
                return FocusBorder.OptionsFactory.get(1.1f, 1.1f);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != mSearchDisposable) {
            mSearchDisposable.dispose();
        }
    }
}
