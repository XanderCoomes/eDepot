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

        // testHPAddProduct(connection);
        // ProductDAO.printProducts(connection);

        // testSimpleOrder(connection); 
        // ProductDAO.printProducts(connection);

        testComplexOrder(connection); 
        ProductDAO.printProducts(connection);


        // testShipmentNotice(connection);
        // ProductDAO.printProducts(connection);

        // testShipmentFill(connection);
        // ProductDAO.printProducts(connection);
    }

    public static void testSimpleOrder(Connection connection) throws SQLException{
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

    public static void testHPAddProduct(Connection connection) throws SQLException{
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

    public static void testDellAddProducts(Connection connection) throws SQLException{
        String stockNum = ProductDAO.newStockNum(connection);
        String manufacturer = "Dell";
        String modelNumber =  "B3470";
        int minStock = 5;
        int quantity = 7;
        int maxStock = 10;
        String locationId = "A9";

        Product p = new Product(stockNum, locationId, manufacturer, modelNumber, quantity, minStock, maxStock, 0);
        ProductDAO.addProduct(connection, p);

        stockNum = ProductDAO.newStockNum(connection);
        manufacturer = "Dell";
        modelNumber =  "C654";
        minStock = 5;
        quantity = 7;
        maxStock = 10;
        locationId = "A8";

        p = new Product(stockNum, locationId, manufacturer, modelNumber, quantity, minStock, maxStock, 0);
        ProductDAO.addProduct(connection, p);
    }
    public static void testComplexOrder(Connection connection) throws SQLException{
        testHPAddProduct(connection);
        testDellAddProducts(connection);
        ProductDAO.printProducts(connection);
        List<Item> orderItems = new ArrayList<>();
        Item item1 = new Item("AA00101", 2);
        Item item2 = new Item("AA00501", 2);
        Item item3 = new Item("AA00601", 2);
        Item item4 = new Item("AA00604", 3);
        Item item5 = new Item("AA00605", 3);
        Item item6 = new Item("AA00201", 2);
        orderItems.add(item1);
        orderItems.add(item2);
        orderItems.add(item3);
        orderItems.add(item4);
        orderItems.add(item5);
        orderItems.add(item6);

        OrderDAO.processOrder(connection, orderItems);


    }

    
}
