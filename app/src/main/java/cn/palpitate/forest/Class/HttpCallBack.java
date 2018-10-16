package cn.palpitate.forest.Class;

import org.jsoup.nodes.Document;

/**
 * Package : cn.palpitate.forest.Class
 * Created : 余悸
 * Date : 2018/9/23 16:55
 */
public interface HttpCallBack {
    void requestSucceed(Document document,boolean isFirstIn,int classIndex,String[] URL_COLLECTIONS);
    void requestSucceed(Document document);
    void requestSucceed(Document document,int nowPage);
    void requestFailed();
}
