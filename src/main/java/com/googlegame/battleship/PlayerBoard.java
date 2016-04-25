package com.googlegame.battleship;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerBoard {

    ArrayList<Battleship> ships;
    HashMap <Integer, Battleship> board;
    int sunkenShips;

    public PlayerBoard(){
        board = new HashMap<Integer, Battleship>();
        ships = new ArrayList<Battleship>();
        sunkenShips = 0;
    }

    public PlayerBoard(ArrayList<Battleship> playerships){
        board = new HashMap<Integer, Battleship>();
        ships = playerships;
        sunkenShips = 0;
    }

    void addPlayerShip(Battleship battleship, Integer[] location){
        ships.add(battleship);
        for(int i = 0; i < location.length; i++){
            board.put(location[i], battleship);
        }
    }

    Battleship checkCoord(int location){
        if(board.containsKey(location)){
            Battleship ship = board.get(location);
            ship.hit();
            return ship;
        }
        return null;
    }

    // Are all the ships sunk
    boolean lost(){
        return ships.size() == sunkenShips;
    }

    void addSunkenShip(){
        sunkenShips++;
    }

}
