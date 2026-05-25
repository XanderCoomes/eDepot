package org.ivc.dbms;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Tester {
    public static void test(Connection connection) throws Exception{
        UtilsDAO.resetDatabase(connection);
        ProductLoader.loadProducts(connection, "data/StarterData.xlsx"); //Load products into database
        ProductDAO.printProducts(connection);

        testAddProducts(connection);
        ProductDAO.printProducts(connection);

        testOrder(connection); 
        ProductDAO.printProducts(connection);

        testShipmentNotice(connection);
        ProductDAO.printProducts(connection);

        testShipmentFill(connection);
        ProductDAO.printProducts(connection);
    }

    public static void testOrder(Connection connection) throws SQLException{
        List<Item> orderItems = new ArrayList<>();
        Item item1 = new Item("AA00101", 2);
        Item item2 = new Item("AA00501", 2);
        Item item3 = new Item("AA00601", 2);
        orderItems.add(item1);
        orderItems.add(item2);
        orderItems.add(item3);
        OrderDAO.processOrder(connection, orderItems);
    }

    public static void testShipmentNotice(Connection connection) throws SQLException{
        List<Item> shipmentItems = new ArrayList<>();
        Item item1 = new Item("AA00101", 2);
        Item item2 = new Item("AA00501", 2);
        Item item3 = new Item("AA00601", 3);
        Item item4 = new Item("AA00603", 2);
        shipmentItems.add(item1);
        shipmentItems.add(item2);
        shipmentItems.add(item3);
        shipmentItems.add(item4);

        String noticeID = "0";
        String carrier  = "FedEx";
        ShipmentDAO.processShipNotice(connection, noticeID, carrier, shipmentItems);
    }

    public static void testShipmentFill(Connection connection) throws SQLException{
        String noticeID = "0"; 
        ShipmentDAO.receiveShipment(connection, noticeID);
    }

    public static void testAddProducts(Connection connection) throws SQLException{
        String stockNum = ProductDAO.newStockNum(connection);
        String manufacturer = "HP";
        String modelNumber =  "A720";
        int minStock = 5;
        int quantity = 7;
        int maxStock = 10;
        String locationId = "A9";

        Product p = new Product(stockNum, locationId, manufacturer, modelNumber, quantity, minStock, maxStock, 0);

        ProductDAO.addProduct(connection, p);
    }



    
}
