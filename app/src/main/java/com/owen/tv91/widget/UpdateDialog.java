package com.owen.tv91.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.owen.tv91.R;
import com.owen.tv91.bean.AppUpdate;
import com.owen.tv91.bean.Qrcode;
import com.owen.tv91.network.NetWorkManager;
import com.owen.tv91.network.response.ResponseTransformer;
import com.owen.tv91.network.schedulers.SchedulerProvider;
import com.owen.tv91.utils.GlideApp;
import com.owen.tv91.utils.InstallUtils;
import com.owen.tv91.utils.ToastUtils;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import eason.linyuzai.download.ELoad;
import eason.linyuzai.download.listeners.MainThreadDownloadListener;
import eason.linyuzai.download.listeners.MainThreadDownloadTaskListener;
import eason.linyuzai.download.listeners.NetPerSecDownloadListener;
import eason.linyuzai.download.task.DownloadTask;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/2/28
 */
public class UpdateDialog extends Dialog implements View.OnClickListener {
    @BindView(R.id.layout_update_dialog_download_layout)
    ViewGroup mDownloadLayout;
    @BindView(R.id.layout_update_dialog_content_tv)
    TextView mContentView;
    @BindView(R.id.layout_update_dialog_btn)
    TextView mButton;
    @BindView(R.id.layout_update_dialog_pb)
    ProgressBar mProgressBar;
    @BindView(R.id.layout_update_dialog_qrcode_layout)
    ViewGroup mQrcodeLayout;
    @BindView(R.id.layout_update_dialog_qrcode_iv)
    ImageView mQrcodeIv;

    private Unbinder bind;
    private AppUpdate mAppUpdate;
    private long mContentLength;

    private Disposable mQrcodeDisposable;

    public UpdateDialog(Context context, AppUpdate appUpdate) {
        super(context, com.owen.player.R.style.EduTvPlayerDialogCommonStyle);
        mAppUpdate = appUpdate;
        init();
    }

    private void init() {
        setContentView(R.layout.layout_update_dialog);
        bind = ButterKnife.bind(this);
        setCancelable(false);
        mProgressBar.setMax(100);
        mProgressBar.setVisibility(View.INVISIBLE);
        mButton.setOnClickListener(this);

        mContentView.setText(mAppUpdate.body);
        mButton.post(new Runnable() {
            @Override
            public void run() {
                mButton.requestFocus();
            }
        });
    }

    @Override
    public void cancel() {
        super.cancel();
        if(null != bind) {
            bind.unbind();
        }
        if(null != mQrcodeDisposable && mQrcodeDisposable.isDisposed()) {
            mQrcodeDisposable.dispose();
        }
    }

    @Override
    public void onClick(View v) {
        v.setEnabled(false);
        mProgressBar.setVisibility(View.VISIBLE);
        mButton.setText("准备下载...");
        String path;
        //这个判断是有SD卡的情况的下载情况路径
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            path = Environment.getExternalStorageDirectory().getPath() + "/91tv/download";
        } else {
            path = v.getContext().getDir("download", Context.MODE_WORLD_READABLE).getPath();
            try {
                Runtime.getRuntime().exec("chmod 777 " + path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.i("zsq", "path="+path);
        String fileName = "91tv.apk";
        File apkFile = new File(path, fileName);
        if(apkFile.exists()) {
            apkFile.delete();
        }

        ELoad eload = new ELoad.Builder(getContext()).build();
        DownloadTask task = eload.url(mAppUpdate.downloadUrl)
                .filepath(path)
                .filename(fileName)
                .downloadListener(new NetPerSecDownloadListener(mButton))
                .downloadListener(new MainThreadDownloadListener() {
                    @Override
                    public void onDownloadBytesReadOnMainThread(DownloadTask task, long bytesLength) {
                        if(mContentLength > 0) {
                            int progress = (int)(bytesLength * 100 / mContentLength);
                            mProgressBar.setProgress(progress);
                        }
                    }

                    @Override
                    public void onDownloadCompleteOnMainThread(DownloadTask task) {
                        mProgressBar.setProgress(100);
                        mButton.setText("下载完成");
                        try {
                            Runtime.getRuntime().exec("chmod 777 " + apkFile.getPath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        InstallUtils.install(getContext(), apkFile);
                        showQrcode(mAppUpdate.downloadUrl);
                    }

                    @Override
                    public void onDownloadContentLengthReadOnMainThread(DownloadTask task, long contentLength) {
                        mContentLength = contentLength;
                    }
                })
                .downloadTaskListener(new MainThreadDownloadTaskListener() {
                    @Override
                    public void onDownloadTaskErrorOnMainThread(DownloadTask task, Throwable e) {
                        e.printStackTrace();
                        ToastUtils.showShortToast("下载失败，请稍后重试！");
//                        UpdateDialog.this.cancel();
                        showQrcode(mAppUpdate.downloadUrl);
                    }
                })
                .create();
        task.start();
    }

    private void showQrcode(String downloadUrl) {
        UpdateDialog.this.setCancelable(true);

        mQrcodeDisposable = NetWorkManager.getRequest().qrcode(downloadUrl, 400, 0)
                .compose(SchedulerProvider.getInstance().applySchedulers())
                .compose(ResponseTransformer.handleResult())
                .subscribe(new Consumer<Qrcode>() {
                    @Override
                    public void accept(Qrcode qrcode) throws Exception {
                        mDownloadLayout.setVisibility(View.INVISIBLE);
                        mQrcodeLayout.setVisibility(View.VISIBLE);
                        GlideApp.with(mQrcodeIv).load(qrcode.qrCodeUrl).into(mQrcodeIv);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtils.showShortToast("二维码信息加载失败！");
                    }
                });
    }
}
