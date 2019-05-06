package com.owen.base.frame;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * @author ZhouSuQiang
 * @date 2018/10/9
 */
public interface MvpBaseView {
    void onPresenterEvent(int code, @Nullable Bundle bundle);
}
