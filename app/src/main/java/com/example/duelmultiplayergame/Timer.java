package com.example.duelmultiplayergame;

import android.os.CountDownTimer;

public class Timer {

    private final long MAX_TIME = 10000;
    private long millisUntil = MAX_TIME;
    private boolean isTimeRunning = false;
    private String str = String.format("%.2f",MAX_TIME);

    private CountDownTimer timer = new CountDownTimer(MAX_TIME, 1) {
        @Override
        public void onTick(long millisUntilFinished) {
            millisUntil = millisUntilFinished;
            UpdateTime();
            isTimeRunning = true;
        }

        @Override
        public void onFinish() {
            UpdateTime();
            isTimeRunning = false;
        }
    };

    private void UpdateTime() {
        float s = (float) millisUntil / 1000;
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
