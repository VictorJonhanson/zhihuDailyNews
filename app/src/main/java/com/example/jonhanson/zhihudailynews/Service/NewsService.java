package com.example.jonhanson.zhihudailynews.Service;

import com.example.jonhanson.zhihudailynews.Model.NewsInfo;
import com.example.jonhanson.zhihudailynews.utils.StreamTool;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NewsService {
    public static List<NewsInfo> getJsonLastNews()throws Exception{
//        new Thread(new Runnable(){
//            @Override
//            public void run() {
                String path = "https://news-at.zhihu.com/api/4/news/latest";
//                try {
                   URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5 * 1000);
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                    if (conn.getResponseCode() == 200) {
                        InputStream inputStream = conn.getInputStream();
                        return parseJSON(inputStream);
                    }
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (ProtocolException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
        return null;
    }

    private static List<NewsInfo> parseJSON(InputStream inputStream) throws Exception{
        List<NewsInfo> newses = new ArrayList<NewsInfo>();
        byte[] data = StreamTool.read(inputStream);
        String json = new String(data);
        JSONObject object = new JSONObject(json);
        JSONArray array = object.getJSONArray("top_stories");
        for(int i = 0; i < array.length(); i++){
            JSONObject jsonObject = array.getJSONObject(i);
            NewsInfo news = new NewsInfo(jsonObject.getString("image"),jsonObject.getString("id"),jsonObject.getString("title"));
            newses.add(news);
        }
        return newses;
    }
}
