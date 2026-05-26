package org.ivc.dbms;

public class Item {
    private final String stockNum;
    private final int quantity;
    private String manufacturer; 
    private String modelNumber; 

    public Item(String stockNum, int quantity) {
        this.stockNum = stockNum;
        this.quantity = quantity;
        this.manufacturer = "NULL";
        this.modelNumber = "NULL";
    }

    public void setManufacturer(String manufacturer){
        this.manufacturer = manufacturer; 
    }  

    public void setModelNumber(String modelNumber){
        this.modelNumber = modelNumber; 
    }

    public String getManufacturer(){
        return manufacturer; 
    }

    public String getModelNumber(){
        return modelNumber;
    }


    public String getStockNum(){
        return stockNum;
   }

    public int getQuantity() {
        return quantity;
    }
}