package org.ivc.dbms;

public class ProductNotFoundException extends Exception {
    public ProductNotFoundException(String manufacturer, String modelNumber) {
        super("No product found with manufacturer: " 
              + manufacturer 
              + " and model_number: " 
              + modelNumber);
    }
}