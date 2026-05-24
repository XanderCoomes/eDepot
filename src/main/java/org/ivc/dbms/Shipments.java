package org.ivc.dbms;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Shipments {
    public static void receiveNotice(Connection connection, String noticeID, String date, List<ShipmentItem> items) throws SQLException{
        String manufacturer; 
        String model_number; 
        int quantity; 
        String stock_num; 
        for(ShipmentItem item: items){
            manufacturer = item.getManufacturer(); 
            model_number = item.getModelNumber();
            quantity = item.getQuantity();
            try{
                stock_num = Products.getStockNum(connection, manufacturer, model_number);
            }catch(SQLException e){ 
                stock_num = Products.addProduct(connection, manufacturer, model_number);
            }
        }
    }

    public static void createNotice(Connection connection, String noticeID, String date){

    }

}