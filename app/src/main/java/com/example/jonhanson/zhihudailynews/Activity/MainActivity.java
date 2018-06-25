package com.example.jonhanson.zhihudailynews.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
    private ViewPager viewPager;
    private TextView title;
    private static List<HashMap<String, Object>> data = new ArrayList<>();
    private static List<ImageView> topnew = new ArrayList<>();
    private static List<NewsInfo> news, top;
    private int currentIndex = 0;
    private boolean isTouch = false;
    private boolean isAuto = true;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                if(isTouch){
                    return;
                }
                currentIndex++;
                if(currentIndex >= 5){
                    currentIndex = -1;
                    currentIndex++;
                }
                viewPager.setCurrentItem(currentIndex);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //关闭线程审查，使联网操作可以在主线程上进行
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        thread.start();
        View view = LayoutInflater.from(this).inflate(R.layout.layout_headloop, null);
        viewPager = view.findViewById(R.id.viewpager);
        title = view.findViewById(R.id.title);
        int i = 0;
        final String[] titles = new String[5];
        final String[] ids = new String[5];
        try {
            top = NewsService.getJsonTopNews();
            if(top != null){
                for (NewsInfo newsInfo : top){
                    ImageView imageView = new ImageView(this);
                    titles[i] = newsInfo.getTitle();
                    ids[i] = newsInfo.getId();
                    i++;
                    imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    final int finalI = i;
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                List<NewsBody> body = NewsService.ShowDetail(ids[finalI - 1]);//传递参数给显示详情方法，参数为新闻id
                                String bodystr = (body.get(0).getBody());
                                Intent intent = new Intent(MainActivity.this, WebActivity.class);
                                intent.putExtra("body", bodystr);
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    Glide.with(this).load(newsInfo.getImages()).into(imageView);
                    topnew.add(imageView);
                }
                if(topnew != null) {
                    MyPagerAdapter pagerAdapter = new MyPagerAdapter();
                    viewPager.setAdapter(pagerAdapter);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                title.setText(titles[position]);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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
            listView.addHeaderView(view);
            listView.setAdapter(adapter);

            //新闻列表的点击详情事件处理
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    HashMap<String, Object> news = data.get(position - 1);
                    String newsid = news.get("id").toString();
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
                    if (lastVisibleItemView != null && lastVisibleItemView.getBottom() == listView.getHeight())
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
                            //新闻列表的点击详情事件处理
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    HashMap<String, Object> news = data.get(position);
                                    String newsid = news.get("id").toString();
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

    private class MyPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            if (topnew.size() > 0) {
                return topnew.size();
            }
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            position %= topnew.size();
            ViewGroup group = (ViewGroup) topnew.get(position).getParent();
            if (group != null) {
                group.removeView(topnew.get(position));
            }
            container.addView(topnew.get(position));
            return topnew.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //无内容操作
        }

    }

    Thread thread = new Thread(new Runnable() {
        public void run() {
            while(isAuto){
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
            }
        }
    });
}
