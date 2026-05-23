package org.ivc.dbms;
public class ShipmentItem {
    private final String modelNumber;
    private final String manufacturer;
    private final int quantity;

    public ShipmentItem(String modelNumber, String manufacturer, int quantity) {
        this.modelNumber = modelNumber;
        this.manufacturer = manufacturer;
        this.quantity = quantity;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public int getQuantity() {
        return quantity;
    }
}