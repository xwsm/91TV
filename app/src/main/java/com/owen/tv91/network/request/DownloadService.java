package com.owen.tv91.network.request;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/2/27
 */
public interface DownloadService {
    @Streaming
    @GET
    Observable<ResponseBody> download(@Header("Accept") String accept, @Url String url);
}
