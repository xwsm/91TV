package com.owen.tv91.utils;

import android.app.Activity;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.ViewGroup;

import com.owen.focus.AbsFocusBorder;
import com.owen.focus.FocusBorder;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/1/18
 */
public class FocusBroderHelper {

    private static AbsFocusBorder.Builder builder() {
        return new FocusBorder.Builder().asColor()
                //阴影宽度(方法shadowWidth(18f)也可以设置阴影宽度)
                .shadowWidth(TypedValue.COMPLEX_UNIT_DIP, 20f)
                //阴影颜色
                .shadowColor(Color.parseColor("#3FBB66"))
                //边框宽度(方法borderWidth(2f)也可以设置边框宽度)
                .borderWidth(TypedValue.COMPLEX_UNIT_DIP, 3.2f)
                //边框颜色
                .borderColor(Color.parseColor("#00FF00"))
                //padding值
                .padding(2f)
                //动画时长
                .animDuration(300)
                //不要闪光动画
                //.noShimmer()
                //闪光颜色
                .shimmerColor(Color.parseColor("#66FFFFFF"))
                //闪光动画时长
                .shimmerDuration(1000)
                //不要呼吸灯效果
                //.noBreathing()
                //呼吸灯效果时长
                .breathingDuration(3000)
                //边框动画模式
                .animMode(AbsFocusBorder.Mode.TOGETHER);
    }

    public static FocusBorder create(Activity activity) {
        return builder().build(activity);
    }

    public static FocusBorder create(ViewGroup viewGroup) {
        return builder().build(viewGroup);
    }
}
