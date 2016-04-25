package com.googlegame.battleship;

public class Battleship {
    private int size;
    private boolean isSunk;
    private int hits;

    public Battleship(int s){
        size = s;
        isSunk = false;
    }

    void setSunk(boolean sunk){
        isSunk = sunk;
    }

    boolean isSunk(){
        return isSunk;
    }

    void hit(){
        hits++;
        if(hits == size) isSunk = true;
    }

}
