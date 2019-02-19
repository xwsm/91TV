package com.owen.tv91.network.request;

import com.owen.tv91.bean.Channel;
import com.owen.tv91.bean.Movie;
import com.owen.tv91.bean.MovieDetail;
import com.owen.tv91.bean.MoviesResult;
import com.owen.tv91.network.response.Response;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Zaifeng on 2018/2/28.
 * 封装请求的接口
 */

public interface Request {

    String HOST = "http://zowen.cn/movies/v1/";

    @POST("channels.api")
    Observable<Response<List<Channel>>> channels();

    @POST("moviesByChannel.api")
    Observable<Response<MoviesResult>> moviesByChannel(@Query("channel") String channel, @Query("page") int page, @Query("size") int size);

    @POST("moviesByChannelAndType.api")
    Observable<Response<MoviesResult>> moviesByChannelAndType(@Query("channel") String channel, @Query("type") String type, @Query("page") int page, @Query("size") int size);

    @POST("detail.api")
    Observable<Response<MovieDetail>> detail(@Query("id") long id);

    @POST("search.api")
    Observable<Response<List<Movie>>> search(@Query("key") String key);



}