package com.example.jonhanson.zhihudailynews.Service;

import com.example.jonhanson.zhihudailynews.Model.NewsInfo;
import com.example.jonhanson.zhihudailynews.utils.StreamTool;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NewsService {
    public static List<NewsInfo> getJsonLastNews(){
        new Thread(new Runnable(){
            @Override
            public void run() {
                String path = "https://news-at.zhihu.com/api/4/news/latest";
                try {
                   URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    System.setProperty("http:keepAlive", "false");
                    if (conn.getResponseCode() == 200) {
                        InputStream inputStream = conn.getInputStream();
                        parseJSON(inputStream);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return null;
    }

    private static List<NewsInfo> parseJSON(InputStream inputStream) throws Exception{
        List<NewsInfo> newses = new ArrayList<NewsInfo>();
        byte[] data = StreamTool.read(inputStream);
        String json = new String(data);
        JSONArray array = new JSONArray(json);
        for(int i = 0; i < array.length(); i++){
            JSONObject jsonObject = array.getJSONObject(i);
            NewsInfo news = new NewsInfo(jsonObject.getString("images"),jsonObject.getString("id"),jsonObject.getString("title"));
            newses.add(news);
        }
        return newses;
    }
}
