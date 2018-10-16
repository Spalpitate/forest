package cn.palpitate.forest.Class;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

/**
 * Package : cn.palpitate.forest.Class
 * Created : 余悸
 * Date : 2018/9/23 15:17
 */
public class ForestSeed extends LitePalSupport implements Serializable {
    private int id;
    private String classFor;
    private String title;
    private String updateTime;
    private String viewers;
    private String address;
    private String ImageUrl;

    public void setId(int id) {
        this.id = id;
    }

    public void setClassFor(String classFor) {
        this.classFor = classFor;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public void setViewers(String viewers) {
        this.viewers = viewers;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public String getClassFor() {
        return classFor;
    }

    public String getTitle() {
        return title;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public String getViewers() {
        return viewers;
    }

    public String getAddress() {
        return address;
    }

    public String getImageUrl() {
        return ImageUrl;
    }
}
