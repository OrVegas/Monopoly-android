package com.example.monopoly;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Player {
    private int playerCellIndex, playerCoins, turnSkip;
    private final int id;
    private ArrayList<Cell>own;
    private final int[]xCoords;
    private final int[] yCoords;
    private boolean userIn;
    public Player(int id, int[] xCoords, int[] yCoords){
        this.xCoords = xCoords;
        this.yCoords = yCoords;
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
    public boolean getUserInGame() {
        return this.userIn;
    }
    public void setUserIn(boolean userIn) {
        this.userIn = userIn;
    }
    public void setPlayerCellIndex(int playerCellIndex) {
        this.playerCellIndex = playerCellIndex % 20;
    }
    public void setPlayerCellIndexJail() {
        this.playerCellIndex = 20;
    }
    public void setPlayerCoins(int playerCoins) {
        this.playerCoins = playerCoins;
    }
    public void setTurnSkip(int turnSkip) {
        this.turnSkip = turnSkip;
    }
    public void addOwn(Cell own) {
        this.own.add(own);
    }
    public void setOwn(ArrayList<Cell> own) {
        this.own = own;
    }
    public void clearPropertyOwnership(){
        this.own.clear();
    }
    public int getXCoords(int index){
        return this.xCoords[index];
    }
    public int getYCoords(int index){
        return this.yCoords[index];
    }
    @NonNull
    @Override
    public String toString(){
        String name ="Error";
        if (this.id == 1) name = "you";
        else if (this.id == 2) name = "bot1";
        else if (this.id == 3) name = "bot2";
        return name;
    }
}
