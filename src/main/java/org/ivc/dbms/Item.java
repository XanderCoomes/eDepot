package org.ivc.dbms;

public class Item {
    private final String stockNum;
    private final int quantity;

    public Item(String stockNum, int quantity) {
        this.stockNum = stockNum;
        this.quantity = quantity;
    }

    public String getStockNum(){
        return stockNum;
   }

    public int getQuantity() {
        return quantity;
    }
}