package com.example.duelmultiplayergame;

import android.os.CountDownTimer;
import android.view.View;

public class ChatTimer extends CountDownTimer
{
    long duration, interval;
    View view;
    boolean isRunning = false;
    public ChatTimer(long millisInFuture, long countDownInterval, View v) {
        super(millisInFuture, countDownInterval);
        // TODO Auto-generated constructor stub
        if (isRunning == true){
            this.cancel();
        }
        isRunning = true;
        this.view = v;
        start();
    }

    @Override
    public void onFinish() {
        view.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onTick(long duration) {
        // could set text for a timer here
    }
}