package com.example.duelmultiplayergame;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

class Cell{
    int x;
    int y;

    int PlayerChoose;

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

    public void setPlayerChoose(int playerChoose) {
        PlayerChoose = playerChoose;
    }

    public int getPlayerChoose() {
        return PlayerChoose;
    }

    public Cell(){
        this.x = -1;
        this.y = -1;
        this.PlayerChoose = 0;
    }
    public Cell(int x, int y , int playerChoose){
        this.x = x;
        this.y = y;
        this.PlayerChoose = playerChoose;
    }

}

public class HistoryPlay {
    private ArrayList<Cell> arrayList;

    public HistoryPlay(){
        this.arrayList = new ArrayList<Cell>();
    }

    public void add(Cell cell){
        this.arrayList.add(cell);
//        Log.d("CELL", "pop: " + Integer.toString(cell.x) + " - " + Integer.toString(cell.y));
    }

    public Cell pop(){
        if (this.arrayList.isEmpty()){
            return null;
        }
        else {
            Cell cell = this.arrayList.get(this.arrayList.size() -1);
            Log.d("TAG", "pop: " + Integer.toString(cell.PlayerChoose));
            this.arrayList.remove(this.arrayList.size() -1);
            return cell;
        }
    }
    public Boolean isEmpty(){
        return this.arrayList.isEmpty();
    }
    
    public void showList(){
        for (int i = 0; i < this.arrayList.size(); i++){
            Log.d("TAG <>-" + Integer.toString(i), "x: " + Integer.toString(this.arrayList.get(i).x) + " ||| y: " + Integer.toString(this.arrayList.get(i).y));
        }
    }
}
