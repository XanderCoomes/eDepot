package org.ivc.dbms;
import java.sql.Connection;

public class ExternalWorld {
    public static void runInterface(Connection connection) throws Exception{
        
        UtilsDAO.resetDatabase(connection);
        displayState(connection);
    }

    public static void displayState(Connection connection) throws Exception{
        ProductDAO.printProducts(connection);
        displayOptions(); 
    }

    public static void displayOptions(){
        System.out.println("Options: ");
        System.out.println("1: Add Order"); 
        System.out.println("2: Deliver Shipping Notice");
        System.out.println("3: Deliver Shipment");
        System.out.println("4: End Interface");
    }
}
