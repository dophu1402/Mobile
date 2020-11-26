package com.example.board;

public class ChessSquare {
    boolean touchOn;
    int player;
    int idX;
    int idY;
    public ChessSquare(boolean nTouch, int nPlayer, int x, int y)
    {
        touchOn = nTouch;
        player = nPlayer;
        idX = x;
        idY = y;
    }

    public ChessSquare()
    {
        touchOn = false;
        player = 0;
        int idX = -1;
        int idY = -1;
    }

    public void setIdX(int id){
        idX = id;
    }

    public void setIdY(int id){
        idY = id;
    }

    public void setPlayer(int p) {
        player = p;
    }

    public void setTouchOn(boolean ot) {
        touchOn = ot;
    }


    public int getIdX(){
        return idX;
    }

    public int getIdY(){
        return idY;
    }

    public boolean getTouchOn() {
        return touchOn;
    }

    public int getPlayer() {
        return player;
    }

}
