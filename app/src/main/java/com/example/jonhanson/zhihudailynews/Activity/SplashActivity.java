package com.example.jonhanson.zhihudailynews.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.jonhanson.zhihudailynews.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        setContentView(R.layout.activity_splash);
        RelativeLayout layoutSplash = (RelativeLayout) findViewById(R.id.activity_splash);
        final Button button = (Button)findViewById(R.id.button);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        alphaAnimation.setDuration(1000);//设置动画播放时长1秒
        layoutSplash.startAnimation(alphaAnimation);
        final boolean[] isClick = {true};

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //页面的跳转
                isClick[0] = false;
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //设置动画监听
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            //动画结束
            @Override
            public void onAnimationEnd(Animation animation) {
                if(isClick[0]) {
                    //页面的跳转
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }
}
