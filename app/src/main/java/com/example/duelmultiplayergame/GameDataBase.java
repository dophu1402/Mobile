package com.example.duelmultiplayergame;

import java.util.ArrayList;

public class GameDataBase {
    public String[] nameGames = { "Caro" };
    public Integer[] wallpapers = { R.drawable.carologo };

    public class DbRecord {
        public String nameGame;
        public Integer wallpaper;
        public DbRecord(String nameGame, Integer wallpaper) { this.nameGame = nameGame; this.wallpaper = wallpaper; }
    }//dbRecord

    public ArrayList<DbRecord> dbList = new ArrayList<DbRecord>();

    // populate the ‘database’ with data items
    public GameDataBase () {
        for(int i = 0; i < nameGames.length; i++){
            dbList.add(new DbRecord(nameGames[i], wallpapers[i]));
        }
    }
}// DATABASE

