package com.owen.data.resource;

import java.util.List;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/1/15
 */
public class MovieDetailBean {
    public String detailUrl;
    public String img;
    public String name; //姓名，无双
    public String sketch; //简述，HD1280高清国语中字版/更新至20190114
    public String score; //评分，7.9
    public String alias; //英文名/别名，Project Gutenberg,Mo seung
    public String director; //导演，庄文强
    public String starring; //主演，周润发,郭富城,张静初,冯文娟
    public String type; //类型，动作片
    public String area; //地区，大陆
    public String language; //语言，国语
    public String showDate; //上映日期，2018
    public String duration; //片长，130
    public String updateDate; //更新日期，2018-12-21
    public String intro; //简介
    public List<PlaySource> playSources; //播放源

    public boolean hasPlaySources() {
        return null != playSources && !playSources.isEmpty();
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{ ");
        sb.append("img = " + img);
        sb.append(" , ");
        sb.append("name = " + name);
        sb.append(" , ");
        sb.append("sketch = " + sketch);
        sb.append(" , ");
        sb.append("score = " + score);
        sb.append(" , ");
        sb.append("alias = " + alias);
        sb.append(" , ");
        sb.append("director = " + director);
        sb.append(" , ");
        sb.append("starring = " + starring);
        sb.append(" , ");
        sb.append("type = " + type);
        sb.append(" , ");
        sb.append("area = " + area);
        sb.append(" , ");
        sb.append("language = " + language);
        sb.append(" , ");
        sb.append("showDate = " + showDate);
        sb.append(" , ");
        sb.append("updateDate = " + updateDate);
        sb.append(" , ");
        sb.append("duration = " + duration);
        sb.append(" , ");
        sb.append("intro = " + intro);
        sb.append(" , ");
        sb.append("playSources = " + playSources);
        sb.append(" }");
        return sb.toString();
    }

    public static class PlaySource {
        public String name;
        public List<PlayUrl> urls;

        @Override
        public String toString() {
            return "{ name = " + name + " , urls = " + urls + " }";
        }
    }

    public static class PlayUrl {
        public String name;
        public String url;

        @Override
        public String toString() {
            return "{ name = " + name + " , url = " + url + " }";
        }
    }
}

