package com.owen.tv91;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tencent.smtt.sdk.VideoActivity;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/3/7
 */
public class X5PlayerActivity extends VideoActivity {

    @Override
    protected void onResume() {
        super.onResume();

        ViewGroup viewGroup = findViewById(android.R.id.content);
        viewGroup.postDelayed(new Runnable() {
            @SuppressLint("ResourceType")
            @Override
            public void run() {
//                logView(level, viewGroup);

//                ViewGroup viewGroup1 = findViewById(0x35);
//                Log.i("zsq", viewGroup1.toString() + " ID="+viewGroup1.getId());
//                ViewGroup viewGroup2 = (ViewGroup) viewGroup1.getChildAt(1);
//                Log.i("zsq", viewGroup2.toString() + " ID="+viewGroup2.getId());
//                ViewGroup viewGroup3 = (ViewGroup) viewGroup2.getChildAt(1);
//                Log.i("zsq", viewGroup3.toString() + " ID="+viewGroup3.getId());
//                TextView textView = (TextView) viewGroup3.getChildAt(1);
//                textView.setText("哈哈哈哈");
            }
        }, 500);

    }

    int level = 1;
    private void logView(int l, ViewGroup viewGroup) {
        Log.i("zsq " + l, viewGroup.toString() + " ID="+viewGroup.getId());
        int size = viewGroup.getChildCount();
        for(int i=0; i<size; i++) {
            View view = viewGroup.getChildAt(i);
            if(view instanceof ViewGroup) {
                logView(level++, (ViewGroup) view);
            } else {
                Log.i("zsq " + l, view.toString() + " ID="+view.getId());
            }
        }
    }
}
