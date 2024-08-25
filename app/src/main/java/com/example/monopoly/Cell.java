package com.example.monopoly;

import java.util.Set;

public class Cell {
    private final String name;
    private final String color;
    private final int price;
    private final int payment;
    private final int sellPrice;
    private int ownedBy;
    private Boolean owned;
    public static final Set<Integer> NON_PROPERTY_CELLS = Set.of(0, 3, 4, 6, 10, 12, 16, 17);

    public Cell(String name, int price, int pay, int sell, String color){
        this.name=name;
        this.price=price;
        this.payment =pay;
        this.owned=false;
        this.ownedBy=0;
        this.sellPrice =sell;
        this.color=color;
    }
    public String getName() {
        return this.name;
    }
    public int getPrice(){
        return this.price;
    }
    public int getPayment(){
        return this.payment;
    }
    public Boolean isOwned(){
        return this.owned;
    }
    public int getOwnedById() {
        return this.ownedBy;
    }
    public int getSellPrice() {
        return sellPrice;
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
