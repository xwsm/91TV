package com.owen.tv91.widget;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.owen.tv91.R;
import com.owen.tv91.bean.AppUpdate;
import com.owen.tv91.utils.InstallUtils;
import com.owen.tv91.utils.ToastUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import eason.linyuzai.download.ELoad;
import eason.linyuzai.download.listeners.MainThreadDownloadListener;
import eason.linyuzai.download.listeners.MainThreadDownloadTaskListener;
import eason.linyuzai.download.listeners.NetPerSecDownloadListener;
import eason.linyuzai.download.task.DownloadTask;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/2/28
 */
public class UpdateDialog extends Dialog implements View.OnClickListener {

    @BindView(R.id.layout_update_dialog_content_tv)
    TextView mContentView;
    @BindView(R.id.layout_update_dialog_btn)
    TextView mButton;
    @BindView(R.id.layout_update_dialog_pb)
    ProgressBar mProgressBar;

    private Unbinder bind;
    private AppUpdate mAppUpdate;
    private long mContentLength;

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
    }

    @Override
    public void onClick(View v) {
        v.setEnabled(false);
        mProgressBar.setVisibility(View.VISIBLE);
        mButton.setText("准备下载...");

        String path = getContext().getCacheDir().getPath() + "/download";
        String fileName = mAppUpdate.downloadUrl.substring(mAppUpdate.downloadUrl.lastIndexOf("/"));
        Log.i("zsq", path);
        Log.i("zsq", fileName);
        Log.i("zsq", mAppUpdate.downloadUrl);
        File apkFile = new File(path + fileName);
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
                        InstallUtils.install(getContext(), apkFile);
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
                        UpdateDialog.this.cancel();
                    }
                })
                .create();
        task.start();
    }
}
