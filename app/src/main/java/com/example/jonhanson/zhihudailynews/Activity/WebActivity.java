package com.example.jonhanson.zhihudailynews.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.jonhanson.zhihudailynews.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class WebActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        Intent intent = getIntent();
        String body = intent.getStringExtra("body");
        WebView webView = (WebView)findViewById(R.id.webview);
        //使用Jsoup调整图片宽度为屏幕宽度
        final Document doc = Jsoup.parse(body);
        final Elements imgs = doc.getElementsByTag("img");
        for (int i = 1; i < imgs.size(); i++) {
            imgs.get(i).attr("style", "width: 100%; height: auto;");
        }
        webView.setWebViewClient(new WebViewClient());
        webView.loadDataWithBaseURL(null, doc.toString(), "text/html", "UTF-8", null);
    }
}
