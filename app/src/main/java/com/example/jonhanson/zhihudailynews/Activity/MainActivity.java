package com.example.jonhanson.zhihudailynews.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.jonhanson.zhihudailynews.Model.NewsInfo;
import com.example.jonhanson.zhihudailynews.R;
import com.example.jonhanson.zhihudailynews.Service.NewsService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = (ListView)this.findViewById(R.id.listView);
        try {
            List<NewsInfo> news = NewsService.getJsonLastNews();
            List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
            for(NewsInfo newsInfo : news){
                HashMap<String, Object> item = new HashMap<String, Object>();
                item.put("images", newsInfo.getImagesUrl());
                item.put("id", newsInfo.getId());
                item.put("title", newsInfo.getTitle());
                data.add(item);
            }
            SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.activity_listview,
                    new String[]{"images","title"}, new int[]{R.id.imageView, R.id.title});
            listView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
