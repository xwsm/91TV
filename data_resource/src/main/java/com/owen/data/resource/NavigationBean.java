package com.owen.data.resource;

import java.util.List;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/1/16
 */
public class NavigationBean {
    public String name;
    public String url;
    public List<NavigationBean> subNavs;

    public boolean hasSubNavs() {
        return null != subNavs && !subNavs.isEmpty();
    }

    @Override
    public String toString() {
        return "{ name=" + name + " ,url=" + url + " ,subNavs=" + subNavs + " }";
    }
}
