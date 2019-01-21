package com.owen.data.resource;

import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/1/16
 */
public class DataResParser {

    private static void logTotalTime(String methodName, long time) {
        long total = System.currentTimeMillis() - time;
        Log.i("zsq", methodName + " , total time = " + (total / 1000f));
    }

    private static String urlSuffix(String url) {
        if(TextUtils.isEmpty(url))
            return "";

        return url.replace(".html", "-pg-1.html");
    }

    public static List<NavigationBean> getNavigations() throws IOException {
        long time = System.currentTimeMillis();
        String baseUrl = "http://www.zuidazyw.com";
        Document doc = Jsoup.connect(baseUrl).get();
        Element sddm = doc.getElementById("sddm");
        Elements lis = sddm.select("li");
        List<NavigationBean> navs = new ArrayList<>();
        for(Element li : lis) {
            Elements as = li.select("a");
            if(as.size() > 0) {
                NavigationBean nav = new NavigationBean();
                nav.name = as.get(0).text();
                nav.url = urlSuffix(as.get(0).absUrl("href"));
                if(nav.url.contains(baseUrl)) {
                    if (as.size() > 1) {
                        nav.subNavs = new ArrayList<>();
                        for (int i = 1; i < as.size(); i++) {
                            NavigationBean subNav = new NavigationBean();
                            subNav.name = as.get(i).text();
                            subNav.url = urlSuffix(as.get(i).absUrl("href"));
                            nav.subNavs.add(subNav);
                        }
                    }
                    navs.add(nav);
                }
            }
        }

        logTotalTime("getNavigations", time);

        return navs;
    }

    public static MovieDetailBean getMovieDetail(String detailUrl) throws Exception {
        MovieDetailBean movieDetailBean = new MovieDetailBean();
        movieDetailBean.detailUrl = detailUrl;
        // 获取影片详情数据
        Document detailDoc = Jsoup.connect(detailUrl).get();
        Elements vodBox = detailDoc.getElementsByClass("vodBox");

        movieDetailBean.img = vodBox.select("img").attr("src");

        Elements vodh = vodBox.select("div.vodh");
        movieDetailBean.name = vodh.select("h2").text();
        movieDetailBean.sketch = vodh.select("span").text();
        movieDetailBean.score = vodh.select("label").text();

        Elements vodinfobox = vodBox.select("ul").select("li");
        if (vodinfobox.size() > 9) {
            movieDetailBean.alias = vodinfobox.get(0).text();
            movieDetailBean.director = vodinfobox.get(1).text();
            movieDetailBean.starring = vodinfobox.get(2).text();
            movieDetailBean.type = vodinfobox.get(3).text();
            movieDetailBean.area = vodinfobox.get(4).text();
            movieDetailBean.language = vodinfobox.get(5).text();
            movieDetailBean.showDate = vodinfobox.get(6).text();
            movieDetailBean.duration = vodinfobox.get(7).text();
            movieDetailBean.updateDate = vodinfobox.get(8).text();
        }

        Elements vodplayinfos = detailDoc.select("div.vodplayinfo");
        if (vodplayinfos.size() > 1) {
            movieDetailBean.intro = vodplayinfos.get(1).text();
        }

        Elements playInfos = new Elements();
        playInfos.add(detailDoc.getElementById("play_1"));
        playInfos.add(detailDoc.getElementById("play_2"));
        playInfos.add(detailDoc.getElementById("down_1"));
        movieDetailBean.playSources = new ArrayList<>();
        for (Element playInfo : playInfos) {
            if (playInfo == null)
                continue;
            Elements playTypes1 = playInfo.select("h3");
            Elements playUrls = playInfo.select("li");
            for (Element playTypeEl : playTypes1) {
                MovieDetailBean.PlaySource playSource = new MovieDetailBean.PlaySource();
                playSource.name = playTypeEl.select("span").text();
                playSource.urls = new ArrayList<>();
                for (Element playUrl : playUrls) {
                    if (!TextUtils.isEmpty(playUrl.text())) {
                        String[] nu = playUrl.text().split("\\$");
                        if (nu.length >= 2) {
                            MovieDetailBean.PlayUrl url1 = new MovieDetailBean.PlayUrl();
                            url1.name = nu[0];
                            url1.url = nu[1];
                            playSource.urls.add(url1);
                        }
                    }
                }
                movieDetailBean.playSources.add(playSource);
            }
        }

        return movieDetailBean;
    }

    private static List<MovieDetailBean> getMovies(Elements elements) {
        List<MovieDetailBean> movies = new ArrayList<>();
        // 获取列表基本数据
        for(Element element : elements) {
            Elements es = element.select("li");
            // 遍历列表
            for(Element el : es) {
                String detailUrl = el.select("a").attr("abs:href");
                if(!TextUtils.isEmpty(detailUrl) && detailUrl.contains("detail")) {
                    try {
                        MovieDetailBean movieDetailBean = getMovieDetail(detailUrl);
                        movies.add(movieDetailBean);
                        Log.i("zsq", movieDetailBean.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            Log.i("zsq", "----------------------------------");
        }

        return movies;
    }

    public static List<MovieDetailBean> getMovies(String url) throws IOException {
        long time = System.currentTimeMillis();

        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.getElementsByClass("xing_vb").select("ul");

        List<MovieDetailBean> movies = getMovies(elements);

        logTotalTime("getMovies", time);

        return movies;
    }

    public static List<MovieDetailBean> search(String word) throws IOException {
        return getMovies("http://www.zuidazyw.com/index.php?m=vod-search-pg-1-wd-" + word + ".html");
    }
}
