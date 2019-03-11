package com.owen.tv91.dao;

import org.litepal.crud.LitePalSupport;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/3/11
 *
 * 观看历史数据表结构，同时也是数据实体
 */
public class HistoryMovie extends LitePalSupport {

    public long id;

    public long movieId;
    public String movieChannel; //所属频道
    public String movieImg;
    public String movieName; //姓名，无双
    public String movieSketch; //简述，HD1280高清国语中字版/更新至20190114
    public String movieScore; //评分，7.9
    public String movieAlias; //英文名/别名，Project Gutenberg,Mo seung
    public String movieType; //类型，动作片
    public String movieShowDate; //上映日期，2018

    public long playSourceId;
    public String playSourceName;

    public long playUrlId;
    public String playUrlName;
    public int playTime;
    public int playDuration;

    public long updateDateMillis;



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }

    public String getMovieChannel() {
        return movieChannel;
    }

    public void setMovieChannel(String movieChannel) {
        this.movieChannel = movieChannel;
    }

    public String getMovieImg() {
        return movieImg;
    }

    public void setMovieImg(String movieImg) {
        this.movieImg = movieImg;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getMovieSketch() {
        return movieSketch;
    }

    public void setMovieSketch(String movieSketch) {
        this.movieSketch = movieSketch;
    }

    public String getMovieScore() {
        return movieScore;
    }

    public void setMovieScore(String movieScore) {
        this.movieScore = movieScore;
    }

    public String getMovieAlias() {
        return movieAlias;
    }

    public void setMovieAlias(String movieAlias) {
        this.movieAlias = movieAlias;
    }

    public String getMovieType() {
        return movieType;
    }

    public void setMovieType(String movieType) {
        this.movieType = movieType;
    }

    public String getMovieShowDate() {
        return movieShowDate;
    }

    public void setMovieShowDate(String movieShowDate) {
        this.movieShowDate = movieShowDate;
    }

    public long getPlaySourceId() {
        return playSourceId;
    }

    public void setPlaySourceId(long playSourceId) {
        this.playSourceId = playSourceId;
    }

    public String getPlaySourceName() {
        return playSourceName;
    }

    public void setPlaySourceName(String playSourceName) {
        this.playSourceName = playSourceName;
    }

    public long getPlayUrlId() {
        return playUrlId;
    }

    public void setPlayUrlId(long playUrlId) {
        this.playUrlId = playUrlId;
    }

    public String getPlayUrlName() {
        return playUrlName;
    }

    public void setPlayUrlName(String playUrlName) {
        this.playUrlName = playUrlName;
    }

    public int getPlayTime() {
        return playTime;
    }

    public void setPlayTime(int playTime) {
        this.playTime = playTime;
    }

    public int getPlayDuration() {
        return playDuration;
    }

    public void setPlayDuration(int playDuration) {
        this.playDuration = playDuration;
    }

    public long getUpdateDateMillis() {
        return updateDateMillis;
    }

    public void setUpdateDateMillis(long updateDateMillis) {
        this.updateDateMillis = updateDateMillis;
    }
}
