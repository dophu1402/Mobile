package com.example.duelmultiplayergame;

public enum Constant_Player {
    RED(1), BLUE(2), PLAYER(1), OPPONENT(2);

    private int value;
    private Constant_Player(int value) {
        this.value = value;
    }
    public int getValue(){
        return value;
    }
}
