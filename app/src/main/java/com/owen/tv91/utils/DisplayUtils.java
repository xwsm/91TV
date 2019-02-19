package com.owen.tv91.utils;

import android.util.TypedValue;

import com.owen.tv91.App;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/2/19
 */
public class DisplayUtils {

    public static float dp2Px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, App.get().getResources().getDisplayMetrics());
    }

}
