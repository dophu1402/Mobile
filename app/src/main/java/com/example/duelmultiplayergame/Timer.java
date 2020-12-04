package com.example.duelmultiplayergame;

import android.os.CountDownTimer;

public class Timer {

    private final long MAX_TIME = 10000;
    private long millisUntil = MAX_TIME;
    private String str = String.format("%.2f",MAX_TIME);

    private CountDownTimer timer = new CountDownTimer(MAX_TIME, 1) {
        @Override
        public void onTick(long millisUntilFinished) {
            millisUntil = millisUntilFinished;
            UpdateTime();
        }

        @Override
        public void onFinish() {
            UpdateTime();
        }
    };

    private void UpdateTime() {
        float s = (float) millisUntil / 1000;
        str = String.format("%.2",s);
    }

    public String GetTimeString() {
        return str;
    }

    public float GetTimeSec() {
        return (float) millisUntil / 1000;
    }

    public void Start() {
        timer.start();
    }

    public void Stop(){
        timer.cancel();
    }
}
