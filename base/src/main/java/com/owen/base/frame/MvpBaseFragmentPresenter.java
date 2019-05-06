package com.owen.base.frame;

import android.content.Context;

/**
 * @author ZhouSuQiang
 * @date 2018/10/10
 */
public abstract class MvpBaseFragmentPresenter<V extends MvpBaseView> extends MvpBasePresenter {
    public void onAttach(Context context) {}
    public void onCreateView() {}
    public void onViewCreated() {}
    public void onActivityCreated() {}
    public void onDestroyView() {}
    public void onDetach() {}
}
