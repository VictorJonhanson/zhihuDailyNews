package com.example.jonhanson.zhihudailynews.Service;

import com.example.jonhanson.zhihudailynews.Model.NewsBody;
import com.example.jonhanson.zhihudailynews.Model.NewsInfo;
import com.example.jonhanson.zhihudailynews.utils.StreamTool;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewsService {
    //向知乎API发起请求获取json数据
    public static List<NewsInfo> getJsonLastNews()throws Exception{
        String path = "http://news-at.zhihu.com/api/4/news/latest";
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        if (conn.getResponseCode() == 200) {
            InputStream inputStream = conn.getInputStream();
            return parseJSON(inputStream);
        }
        return null;
    }

    //获取得到的新闻数据进行解析
    private static List<NewsInfo> parseJSON(InputStream inputStream) throws Exception{
        List<NewsInfo> newses = new ArrayList<>();
        byte[] data = StreamTool.read(inputStream);
        String json = new String(data);
        JSONObject object = new JSONObject(json);
        JSONArray array = object.getJSONArray("stories");//获取的数据头为stories
        for(int i = 0; i < array.length(); i++){
            JSONObject jsonObject = array.getJSONObject(i);
            JSONArray image = jsonObject.getJSONArray("images");
            NewsInfo news = new NewsInfo(image.getString(0),jsonObject.getString("id"),jsonObject.getString("title"));
            newses.add(news);
        }
        return newses;
    }

    //根据新闻id获取指定新闻的json数据
    public static List<NewsBody> ShowDetail(String newsid) throws Exception{
        String path = "http://news-at.zhihu.com/api/4/news/" + newsid;
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        if (conn.getResponseCode() == 200) {
            InputStream inputStream = conn.getInputStream();
            return parseWeb(inputStream);
        }
        return null;
    }

    //获取得到的新闻内容进行解析
    private static List<NewsBody> parseWeb(InputStream inputStream) throws Exception{
        List<NewsBody> body = new ArrayList<>();
        byte[] data = StreamTool.read(inputStream);
        String json = new String(data);
        JSONObject object = new JSONObject(json);
        NewsBody news = new NewsBody(object.getString("body"));
        body.add(news);
        return body;
    }



    public static List<NewsInfo> getJsonBeforeNews() throws Exception{
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");//获取当前年月日
        Date date = new Date(System.currentTimeMillis());
        String beforedate = simpleDateFormat.format(date);
        int num = Integer.parseInt(beforedate);
        String path = "http://news.at.zhihu.com/api/4/news/before/" + (num);
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        if (conn.getResponseCode() == 200) {
            InputStream inputStream = conn.getInputStream();
            return parseJSON(inputStream);
        }
        return null;
    }
}
