package com.example.duelmultiplayergame;

import android.os.CountDownTimer;

public class Timer {

    private long MAX_TIME;
    private long millisUntil;
    private boolean isTimeRunning = false;
    private String str;

    public Timer () {
        MAX_TIME = 0;
        millisUntil = MAX_TIME;
        UpdateTime(millisUntil);
    }
    public void SetMaxTime(long Second){
        MAX_TIME = Second;
        millisUntil = MAX_TIME;
        UpdateTime(millisUntil);
    }

    private CountDownTimer timer = new CountDownTimer(MAX_TIME, 1) {
        @Override
        public void onTick(long millisUntilFinished) {
            millisUntil = millisUntilFinished;
            UpdateTime(millisUntil);
            isTimeRunning = true;
        }

        @Override
        public void onFinish() {
            UpdateTime(millisUntil);
            isTimeRunning = false;
        }
    };

    private void UpdateTime(long millisSecond) {
        float s = (float) millisSecond / 1000;
        str = String.format("%.2",s);
    }

    //Trả về chuỗi thời gian
    public String GetTimeString() {
        return str;
    }

    //Trả về thời gian còn lại.
    public float GetTimeSec() {
        return (float) millisUntil / 1000;
    }

    //Bắt đầu tính giờ
    public void Start() {
        timer.start();
    }

    //Dừng tính giờ
    public void Stop(){
        timer.cancel();
    }

    //Tín hiệu thời gian có còn chạy hay không, true là đang chạy.
    public boolean isRunning() {
        return isTimeRunning;
    }
}
