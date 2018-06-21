package com.example.jonhanson.zhihudailynews.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.jonhanson.zhihudailynews.Model.NewsBody;
import com.example.jonhanson.zhihudailynews.Model.NewsInfo;
import com.example.jonhanson.zhihudailynews.R;
import com.example.jonhanson.zhihudailynews.Service.NewsService;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    private ListView listView;
    private static List<HashMap<String, Object>> data = new ArrayList<>();
    private static List<NewsInfo> news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //关闭线程审查，使联网操作可以在主线程上进行
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        final SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.activity_listview,
                new String[]{"image","title"}, new int[]{R.id.imageView, R.id.title});
        try {
            news = NewsService.getJsonLastNews();//调用方法获取解析后的新闻列表
            if (news != null) {
                for(NewsInfo newsInfo : news){
                    HashMap<String, Object> item = new HashMap<>();
                    URL picUrl = new URL(newsInfo.getImages());
                    Bitmap pngBM = BitmapFactory.decodeStream(picUrl.openStream());
                    item.put("image", pngBM);
                    item.put("id", newsInfo.getId());
                    item.put("title", newsInfo.getTitle());
                    data.add(item);
                }
            }
            listView = this.findViewById(R.id.listView);
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
            listView.setAdapter(adapter);

            //新闻列表的点击详情事件处理
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    NewsInfo newsInfo = news.get(position);
                    String newsid = newsInfo.getId();
                    try {
                        List<NewsBody> body = NewsService.ShowDetail(newsid);//传递参数给显示详情方法，参数为新闻id
                        String bodystr = (body.get(0).getBody());
                        Intent intent = new Intent(MainActivity.this, WebActivity.class);
                        intent.putExtra("body", bodystr);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        //滑动列表到顶部以及底部的操作
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    View firstVisibleItemView = listView.getChildAt(0);
                    if (firstVisibleItemView != null && firstVisibleItemView.getTop() == 0) {
                        //滑动到顶部的操作，拉到顶部会一直执行
                    }
                } else if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                    View lastVisibleItemView = listView.getChildAt(listView.getChildCount() - 1);
                    if (lastVisibleItemView != null && lastVisibleItemView.getBottom() == listView.getHeight()) {
                        try {
                            news = NewsService.getJsonBeforeNews();//调用方法获取解析后的新闻列表
                            if (news != null) {
                                for (NewsInfo newsInfo : news) {
                                    HashMap<String, Object> item = new HashMap<>();
                                    URL picUrl = new URL(newsInfo.getImages());
                                    Bitmap pngBM = BitmapFactory.decodeStream(picUrl.openStream());
                                    item.put("image", pngBM);
                                    item.put("id", newsInfo.getId());
                                    item.put("title", newsInfo.getTitle());
                                    data.add(item);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //无内容操作。
            }
        });
    }

    //连续点击两次返回键退出应用
    private static Boolean isQuit = false;
    private Timer timer = new Timer();
    @Override
    public void onBackPressed() {
        if(!isQuit) {
            isQuit = true;
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    isQuit = false;
                }
            };
            timer.schedule(task, 2000);
        }else{
            finish();
        }

    }
}
