package com.example.duelmultiplayergame;

public enum  Constant_Name_Game {
    CARO(0), COMMING_GAME(999);

    private int value;
    private Constant_Name_Game(int value) {
        this.value = value;
    }
    public int getValue(){
        return value;
    }
}


