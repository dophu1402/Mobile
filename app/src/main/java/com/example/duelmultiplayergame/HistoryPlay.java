package com.example.duelmultiplayergame;

import androidx.annotation.NonNull;

import java.util.List;

class Step{
    int x;
    int y;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

}

public class HistoryPlay {
    List<Step> step;
    
}
