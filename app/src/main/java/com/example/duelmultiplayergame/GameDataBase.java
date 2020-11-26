package com.example.duelmultiplayergame;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;

import java.util.ArrayList;

public class GameDataBase {
    public String[] nameGames = { "Caro", "Coming Soon"};
    public Integer[] wallpapers = { R.drawable.menucaro, R.drawable.menuchess };
    public Integer[] colorBG = {R.color.lightBlue, R.color.lightGreen};

    public class DbRecord {
        public String nameGame;
        public Integer wallpaper;
        public Integer color;
        public DbRecord(String nameGame, Integer wallpaper, Integer color) { this.nameGame = nameGame; this.wallpaper = wallpaper; this.color = color; }
    }//dbRecord

    public ArrayList<DbRecord> dbList = new ArrayList<DbRecord>();

    // populate the ‘database’ with data items
    public GameDataBase () {
        for(int i = 0; i < nameGames.length; i++){
            dbList.add(new DbRecord(nameGames[i], wallpapers[i], colorBG[i]));
        }
    }
}// DATABASE

