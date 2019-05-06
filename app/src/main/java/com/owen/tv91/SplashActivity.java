package com.owen.tv91;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-04-29
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        Log.i("zsq", "SplashActivity onCreate...");


        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
