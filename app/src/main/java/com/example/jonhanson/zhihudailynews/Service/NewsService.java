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
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");//获取当前年月日
    private static Date date = new Date(System.currentTimeMillis());
    private static String beforedate = simpleDateFormat.format(date);
    private static int num = Integer.parseInt(beforedate);

    //向知乎API发起请求获取最新新闻json数据
    public static List<NewsInfo> getJsonTopNews() throws Exception {
        String path = "http://news-at.zhihu.com/api/4/news/latest";
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        if (conn.getResponseCode() == 200) {
            InputStream inputStream = conn.getInputStream();
            return parseTopJSON(inputStream);
        }
        return null;
    }

    //获取得到的接送新闻头条数据进行解析
    private static List<NewsInfo> parseTopJSON(InputStream inputStream) throws Exception{
        List<NewsInfo> newses = new ArrayList<>();
        byte[] data = StreamTool.read(inputStream);
        String json = new String(data);
        JSONObject object = new JSONObject(json);
        JSONArray array = object.getJSONArray("top_stories");//获取的数据头为top_stories
        for(int i = 0; i < array.length(); i++){
            JSONObject jsonObject = array.getJSONObject(i);
            NewsInfo news = new NewsInfo(jsonObject.getString("image"),jsonObject.getString("id"),jsonObject.getString("title"));
            newses.add(news);
        }
        return newses;
    }

    //向知乎API发起请求获取最新新闻json数据
    public static List<NewsInfo> getJsonLastNews() throws Exception {
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

    //获取得到的接送新闻数据进行解析
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

    //获取得到的json新闻内容进行解析
    private static List<NewsBody> parseWeb(InputStream inputStream) throws Exception{
        List<NewsBody> body = new ArrayList<>();
        byte[] data = StreamTool.read(inputStream);
        String json = new String(data);
        JSONObject object = new JSONObject(json);
        NewsBody news = new NewsBody(object.getString("body"));
        body.add(news);
        return body;
    }

    //根据当前日期获取往日json新闻数据
    public static List<NewsInfo> getJsonBeforeNews()  throws Exception{
        num--;
        String path = "http://news.at.zhihu.com/api/4/news/before/" + (num);
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        if (conn.getResponseCode() == 200) {
            InputStream inputStream = conn.getInputStream();
            //解析新闻数据使用先前定义的方法
            return parseJSON(inputStream);
        }
        return null;
    }
}
