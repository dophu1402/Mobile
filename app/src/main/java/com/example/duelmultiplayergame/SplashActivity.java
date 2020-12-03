package com.example.duelmultiplayergame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity {
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        Log.d("TAG", "onCreate: ");
        // set animate for logo
        FrameLayout mainFrame = ((FrameLayout) findViewById(R.id.frameSplash));
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this,
                R.anim.showing);
        mainFrame.startAnimation(hyperspaceJumpAnimation);
        Log.d("TAG", "onCreate: 1");
        // change activity after animate
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MenuNavigationActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                Log.d("TAG", "onCreate: 2");
            }
        };
        long delay = 7000L;
        Timer timer = new Timer("Timer");Log.d("TAG", "onCreate: 3");
        timer.schedule(timerTask, delay);
    }
}