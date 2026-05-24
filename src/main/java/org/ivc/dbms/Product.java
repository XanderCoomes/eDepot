package org.ivc.dbms;

public class Product {
    private final String stockNum;
    private final String locationId;
    private final String manufacturer;
    private final String modelNumber;
    private final int quantity;
    private final int minStockLevel;
    private final int maxStockLevel;
    private final int replenishment;

    public Product(String stockNum, String locationId, String manufacturer, String modelNumber, int quantity, int minStockLevel, int maxStockLevel, int replenishment) {
        this.stockNum = stockNum;
        this.locationId = locationId;
        this.manufacturer = manufacturer;
        this.modelNumber = modelNumber;
        this.quantity = quantity;
        this.minStockLevel = minStockLevel;
        this.maxStockLevel = maxStockLevel;
        this.replenishment = replenishment;
    }

    public String getStockNum() {
        return stockNum;
    }

    public String getLocationId() {
        return locationId;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getMinStockLevel() {
        return minStockLevel;
    }

    public int getMaxStockLevel() {
        return maxStockLevel;
    }

    public int getReplenishment() {
        return replenishment;
    }
}