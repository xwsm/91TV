package com.owen.player;

import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/3/23
 */
public interface PlayProgressListener {
    void onPlayProgress(@Nullable Intent data);
}
