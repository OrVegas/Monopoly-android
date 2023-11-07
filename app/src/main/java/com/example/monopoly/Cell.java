package com.example.monopoly;

public class Cell {
    private final String name;
    private final String color;
    private final int price;
    private final int pay;
    private final int sell;
    private int ownedBy;
    private Boolean owned;
    public Cell(String name, int price, int pay, int sell, String color){
        this.name=name;
        this.price=price;
        this.pay=pay;
        this.owned=false;
        this.ownedBy=0;
        this.sell=sell;
        this.color=color;
    }
    public String getName() {
        return this.name;
    }
    public int getPrice(){
        return this.price;
    }
    public int getPay(){
        return this.pay;
    }
    public Boolean getOwned(){
        return this.owned;
    }
    public int getOwnedBy() {
        return this.ownedBy;
    }
    public int getSell() {
        return sell;
    }
    public String getColor() {
        return color;
    }
    public void setOwned(Boolean owned) {
        this.owned = owned;
    }
    public void setOwnedBy(int ownedBy) {
        this.ownedBy=ownedBy;
    }
}
