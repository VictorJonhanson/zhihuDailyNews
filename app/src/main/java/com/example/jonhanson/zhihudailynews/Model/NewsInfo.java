package com.example.jonhanson.zhihudailynews.Model;

public class NewsInfo {
    private String imagesUrl;//新闻列表的图片地址
    private String id;//新闻的id
    private String title;//新闻的标题

    public NewsInfo(){}

    public NewsInfo(String imagesUrl,String id,String title){
        this.imagesUrl = imagesUrl;
        this.id = id;
        this.title = title;
    }

    public String getImagesUrl() {
        return imagesUrl;
    }

    public void setImagesUrl(String imagesUrl) {
        this.imagesUrl = imagesUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
