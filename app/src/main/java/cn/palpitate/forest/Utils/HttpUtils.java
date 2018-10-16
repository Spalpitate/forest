package cn.palpitate.forest.Utils;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

import cn.palpitate.forest.Activity.MainActivity;
import cn.palpitate.forest.Activity.WelcomeActivity;
import cn.palpitate.forest.Class.DataSaveSucceed;
import cn.palpitate.forest.Class.ForestSeed;
import cn.palpitate.forest.Class.HttpCallBack;
import cn.palpitate.forest.Class.LocalForestSeed;
import cn.palpitate.forest.Fragment.SettingFragment;

/**
 * Package : cn.palpitate.forest.Utils
 * Created : 余悸
 * Date : 2018/9/23 16:26
 */
public class HttpUtils {
    public static void getDocuments(final String requestUrl, final HttpCallBack callBack, final boolean isFirstIn,final int classIndex,final String[] URL_COLLECTIONS) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect(requestUrl)
                            .userAgent(WelcomeActivity.USER_AGENT)
                            .get();
                    callBack.requestSucceed(document,isFirstIn,classIndex,URL_COLLECTIONS);
                } catch (IOException e) {
                    callBack.requestFailed();
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public static void getDocuments(final String requestUrl, final HttpCallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect(requestUrl)
                            .userAgent(WelcomeActivity.USER_AGENT)
                            .get();
                    callBack.requestSucceed(document);
                } catch (IOException e) {
                    callBack.requestFailed();
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public static void getDocuments(final String requestUrl, final HttpCallBack callBack, final int nowPage) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect(requestUrl)
                            .userAgent(WelcomeActivity.USER_AGENT)
                            .get();
                    callBack.requestSucceed(document,nowPage);
                } catch (IOException e) {
                    callBack.requestFailed();
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public static void parseDocumentForHomePage(int pos,int classId,Document document,List<ForestSeed> forestSeedList) {
        boolean isOnly = true;      //有无重复
        Elements elementsForTI,elementsForUV,elementsForA;
        for (int i=1;i<=6;i++) {
            elementsForTI = document.select("body > div.detail > div > div.detail_right > div.detail_right_tab > div > div:nth-child("+pos+") > ul > li:nth-child("+i+") > p.img > img");
            elementsForUV = document.select("body > div.detail > div > div.detail_right > div.detail_right_tab > div > div:nth-child("+pos+") > ul > li:nth-child("+i+") > p:nth-child(3)");
            elementsForA = document.select("body > div.detail > div > div.detail_right > div.detail_right_tab > div > div:nth-child("+pos+") > ul > li:nth-child("+i+") > p.img > a");
            ForestSeed forestSeed = new ForestSeed();
            forestSeed.setClassFor(MainActivity.HOME_PAGE_TITLE[classId]);
            forestSeed.setTitle(elementsForTI.attr("title"));
            forestSeed.setImageUrl(elementsForTI.attr("data-original"));
            forestSeed.setUpdateTime(elementsForUV.text().split(" ")[0]);
            forestSeed.setViewers(elementsForUV.text().split(" ")[1]);
            forestSeed.setAddress(MainActivity.requestUrl+elementsForA.attr("href"));
            for (ForestSeed tmp:forestSeedList) {
                if (tmp.getAddress().equals(forestSeed.getAddress())) {
                    isOnly = false;
                    break;
                }
            }
            if (isOnly) {
                forestSeedList.add(forestSeed);
            } else {
                continue;
            }
        }
    }
    public static void parseDocumentForCommon(int classId,Document document,List<ForestSeed> forestSeedList) {
        forestSeedList.clear();
        boolean isOnly = true;
        Elements elementsForTI,elementsForUV,elementsForA;
        for (int i=1;i<=21;i++) {
            elementsForTI = document.select("body > div.detail > div > div.detail_right > div.detail_right_tab > div > div > ul:nth-child(2) > li:nth-child("+i+") > p:nth-child(1) > img");
            elementsForUV = document.select("body > div.detail > div > div.detail_right > div.detail_right_tab > div > div > ul:nth-child(2) > li:nth-child("+i+") > p:nth-child(3)");
            elementsForA = document.select("body > div.detail > div > div.detail_right > div.detail_right_tab > div > div > ul:nth-child(2) > li:nth-child("+i+") > p:nth-child(1) > a");
            ForestSeed forestSeed = new ForestSeed();
            forestSeed.setClassFor(MainActivity.HOME_PAGE_TITLE[classId]);
            if (elementsForTI.attr("title") == "") {
                break;
            }
            forestSeed.setTitle(elementsForTI.attr("title"));
            forestSeed.setImageUrl(elementsForTI.attr("data-original"));
            forestSeed.setUpdateTime(elementsForUV.text().split(" ")[0]);
            forestSeed.setViewers(elementsForUV.text().split(" ")[1]);
            forestSeed.setAddress(MainActivity.requestUrl+elementsForA.attr("href"));
            forestSeedList.add(forestSeed);
            for (ForestSeed tmp:forestSeedList) {
                if (tmp.getAddress().equals(forestSeed.getAddress())) {
                    isOnly = false;
                    break;
                }
            }
            if (isOnly) {
                forestSeedList.add(forestSeed);
            } else {
                continue;
            }
        }
    }
    //本地化网站数据源
    public static void parseDocumentForCommon(Document document, DataSaveSucceed saveSucceed) {
        Elements elementsForTI,elementsForUV,elementsForA;
        for (int i=1;i<=21;i++) {
            elementsForTI = document.select("body > div.detail > div > div.detail_right > div.detail_right_tab > div > div > ul:nth-child(2) > li:nth-child("+i+") > p:nth-child(1) > img");
            elementsForUV = document.select("body > div.detail > div > div.detail_right > div.detail_right_tab > div > div > ul:nth-child(2) > li:nth-child("+i+") > p:nth-child(4)");
            elementsForA = document.select("body > div.detail > div > div.detail_right > div.detail_right_tab > div > div > ul:nth-child(2) > li:nth-child("+i+") > p:nth-child(1) > a");
            LocalForestSeed forestSeed = new LocalForestSeed();
            if (elementsForTI.attr("title") == "") {
                break;
            }
            forestSeed.setTitle(elementsForTI.attr("title"));
            forestSeed.setImageUrl(elementsForTI.attr("src"));
            //Log.d("Search",elementsForUV.text());
            forestSeed.setUpdateTime(elementsForUV.text().split(" ")[0]);
            forestSeed.setViewers(elementsForUV.text().split(" ")[1]);
            forestSeed.setAddress(MainActivity.requestUrl+elementsForA.attr("href"));
            forestSeed.save();
        }
        saveSucceed.goOnParseSave();
    }
    public static String parseDocumentForVideo(Document document) {
        String videoUrl = document.select("body > div.detail > div > div.detail_right > div.detail_right_tab > div.gc_video_content > div.gc_vodeo_left > div:nth-child(3) > a")
                .text();
        return videoUrl;
    }
    public static void parseDocumentForPagenums(Document document,List<String> pageNums) {      //只需调用一次
        pageNums.clear();
        Elements elementsForPN;
        for (int i=4;i<=12;i++) {
            elementsForPN = document.select("body > div.detail > div > div.detail_left > ul > li > ol > li:nth-child("+i+") > span");
            String pageNum = elementsForPN.text();
            pageNums.add(String.valueOf((int)Math.ceil(Integer.parseInt(pageNum) / 21.0)));
        }
    }
}
