package org.ivc.dbms;

public class ShipmentItem {
    private final String manufacturer;
    private final String modelNumber;
    private final int quantity;

    public ShipmentItem(String manufacturer, String modelNumber, int quantity) {
        this.manufacturer = manufacturer;
        this.modelNumber = modelNumber;
        this.quantity = quantity;
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
}