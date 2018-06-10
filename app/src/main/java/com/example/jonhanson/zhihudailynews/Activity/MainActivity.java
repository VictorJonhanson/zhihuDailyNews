package com.example.jonhanson.zhihudailynews.Activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.jonhanson.zhihudailynews.Model.NewsInfo;
import com.example.jonhanson.zhihudailynews.R;
import com.example.jonhanson.zhihudailynews.Service.NewsService;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        ListView listView = (ListView)this.findViewById(R.id.listView);
        try {
            List<NewsInfo> news = NewsService.getJsonLastNews();
            List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
            for(NewsInfo newsInfo : news){
                HashMap<String, Object> item = new HashMap<String, Object>();
                URL picUrl = new URL(newsInfo.getImages());
                Bitmap pngBM = BitmapFactory.decodeStream(picUrl.openStream());
                item.put("image", pngBM);
                item.put("id", newsInfo.getId());
                item.put("title", newsInfo.getTitle());
                data.add(item);
            }
            SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.activity_listview,
                    new String[]{"image","title"}, new int[]{R.id.imageView, R.id.title});
            listView.setAdapter(adapter);
            adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                public boolean setViewValue(View arg0, Object arg1,
                                            String textRepresentation) {
                    if ((arg0 instanceof ImageView) & (arg1 instanceof Bitmap)) {
                        ImageView imageView = (ImageView) arg0;
                        Bitmap bitmap = (Bitmap) arg1;
                        imageView.setImageBitmap(bitmap);
                        return true;
                    }else
                        return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
