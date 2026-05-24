package org.ivc.dbms;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Shipments {

    
    //precondition: all products are already in the depot
    public static void setReplenishments(Connection connection, String noticeID, String date, List<ShipmentItem> items) throws SQLException{
        String manufacturer; 
        String modelNumber; 
        int quantity; 
        String stockNum; 
        for(ShipmentItem item: items){
            manufacturer = item.getManufacturer(); 
            modelNumber = item.getModelNumber();
            quantity = item.getQuantity();
            stockNum = Products.getStockNum(connection, manufacturer, modelNumber);
            Products.setReplenishment(connection, stockNum, quantity);
        }
    }
}