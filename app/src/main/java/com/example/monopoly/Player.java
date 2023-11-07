package com.example.monopoly;

import java.util.ArrayList;

public class Player {
    private int playerCellIndex, playerCoins, turnSkip,id;
    private ArrayList<Cell>own;
    private boolean userIn;
    public Player(int id){
        this.playerCellIndex=0;
        this.playerCoins=2000;
        this.own=new ArrayList<>();
        this.turnSkip=0;
        this.userIn=true;
        this.id=id;
    }
    public int getPlayerCellIndex() {
        return this.playerCellIndex;
    }
    public ArrayList<Cell> getOwn() {
        return this.own;
    }
    public int getPlayerCoins() {
        return this.playerCoins;
    }
    public int getTurnSkip() {
        return this.turnSkip;
    }
    public int getId() {
        return id;
    }
    public boolean isUserIn() {
        return this.userIn;
    }
    public void setPlayerCellIndex(int playerCellIndex) {
        this.playerCellIndex = playerCellIndex;
    }
    public void setPlayerCoins(int playerCoins) {
        this.playerCoins = playerCoins;
    }
    public void setTurnSkip(int turnSkip) {
        this.turnSkip = turnSkip;
    }
    public void setUserIn(boolean userIn) {
        this.userIn = userIn;
    }
    public void addOwn(Cell own) {
        this.own.add(own);
    }
    public void setOwn(ArrayList<Cell> own) {
        this.own = own;
    }
    public void clearOwn(){
        this.own.clear();
    }
}
