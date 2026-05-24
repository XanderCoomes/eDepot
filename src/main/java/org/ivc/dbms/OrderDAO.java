package org.ivc.dbms;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderDAO {
    public static void fillOrderItem(Connection connection, Item orderItem) throws SQLException{
        int oldQuantity = ProductDAO.getQuantity(connection, orderItem.getStockNum());
        int newQuantity = oldQuantity - orderItem.getQuantity(); 
        ProductDAO.setQuantity(connection, orderItem.getStockNum(), newQuantity);
    }

    public static void fillOrderItems(Connection connection, List<Item> orderItems) throws SQLException{
        for (Item orderItem: orderItems){
            fillOrderItem(connection, orderItem); 
        }
    }

    public static void sendReplenishmentOrder(Connection connection, String manufacturer, List<String> lowStockNums) throws SQLException {
        if (lowStockNums == null || lowStockNums.isEmpty()) {
            System.out.println("No replenishment items for manufacturer: " + manufacturer);
            return;
        }
    
        String placeholders = String.join(", ", Collections.nCopies(lowStockNums.size(), "?"));
    
        String query = """
                SELECT manufacturer, model_number
                FROM PRODUCTS
                WHERE manufacturer = ?
                  AND stock_num IN (%s)
                """.formatted(placeholders);
    
        System.out.println("Replenishment order for manufacturer: " + manufacturer);
        System.out.println("Items to replenish:");
    
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, manufacturer);
    
            for (int i = 0; i < lowStockNums.size(); i++) {
                statement.setString(i + 2, lowStockNums.get(i));
            }
    
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String resultManufacturer = resultSet.getString("manufacturer");
                    String modelNumber = resultSet.getString("model_number");
    
                    System.out.println("(" + resultManufacturer + ", " + modelNumber + ")");
                }
            }
        }
    }

    public static void processOrder(Connection connection, List<Item> orderItems) throws SQLException{
        fillOrderItems(connection, orderItems); 
        List<String> manufacturers = getDistinctManufacturersByOrderItems(connection, orderItems);
        List<String> lowStockNums;
        for(String manufacturer: manufacturers){
            if(isManufacturerLow(connection, manufacturer)){
                lowStockNums = getStockNumsBelowMaxByManufacturer(connection, manufacturer); 
                sendReplenishmentOrder(connection, manufacturer, lowStockNums); 
            }
        }
    }

    public static List<String> getDistinctManufacturersByOrderItems(Connection connection, List<Item> orderItems) throws SQLException {
        if (orderItems == null || orderItems.isEmpty()) {
            return new ArrayList<>();
        }

        String placeholders = String.join(
                ", ",
                Collections.nCopies(orderItems.size(), "?")
        );

        String query = """
                SELECT DISTINCT manufacturer
                FROM PRODUCTS
                WHERE stock_num IN (%s)
                """.formatted(placeholders);

        List<String> manufacturers = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (int i = 0; i < orderItems.size(); i++) {
                statement.setString(i + 1, orderItems.get(i).getStockNum());
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    manufacturers.add(resultSet.getString("manufacturer"));
                }
            }
        }

        return manufacturers;
    }

    public static List<String> getStockNumsBelowMaxByManufacturer(Connection connection, String manufacturer) throws SQLException {
        String query = """
                SELECT stock_num
                FROM PRODUCTS
                WHERE manufacturer = ?
                AND quantity < max_stock_level
                AND replenishment = 0
                """;

        List<String> stockNums = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, manufacturer);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    stockNums.add(resultSet.getString("stock_num"));
                }
            }
        }

        return stockNums;
    }

    public static boolean isManufacturerLow(Connection connection, String manufacturer) throws SQLException {
        String query = """
                SELECT COUNT(DISTINCT stock_num) AS low_count
                FROM PRODUCTS
                WHERE manufacturer = ?
                AND quantity < min_stock_level
                AND replenishment = 0
                """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, manufacturer);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int lowCount = resultSet.getInt("low_count");
                    return lowCount >= 3;
                }
            }
        }

        return false;
    }

}
