package org.ivc.dbms;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//data access object
public class ProductDAO {

    public static void addProduct(Connection connection, String stockNum, String locationID, String manufacturer, String modelNumber, int minStockLevel, int maxStockLevel, int quantity) throws SQLException {
        String query = """
                INSERT INTO PRODUCTS (
                    stock_num,
                    location_id,
                    manufacturer,
                    model_number,
                    quantity,
                    min_stock_level,
                    max_stock_level,
                    replenishment
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, stockNum);
            statement.setString(2, locationID);      
            statement.setString(3, manufacturer);
            statement.setString(4, modelNumber);
            statement.setInt(5, quantity);
            statement.setInt(6, minStockLevel);
            statement.setInt(7, maxStockLevel);
            statement.setInt(8, 0);                // no replenishment initially

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted != 1) {
                throw new SQLException("Product insert failed for stock_num: " + stockNum);
            }
        }
    }
    
    public static String newStockNum(Connection connection) throws SQLException{
        String query = """
            SELECT MAX(stock_num) AS max_stock_num
            FROM PRODUCTS
            """;

        try (
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery()
        ) {
            if (resultSet.next()) {
                String maxStockNum = resultSet.getString("max_stock_num");

                if (maxStockNum == null) {
                    return "AA00000";
                }

                return incrementStockNum(maxStockNum);
            }

            return "AA00000";
        }

    }

    private static String incrementStockNum(String stockNum) throws SQLException {
        String prefix = stockNum.substring(0, 2);   // "AA"
        String numberPart = stockNum.substring(2); // "00042"
    
        int number = Integer.parseInt(numberPart);
    
        if (number < 99999) {
            number++;
            return prefix + String.format("%05d", number);
        }
    
        String nextPrefix = incrementPrefix(prefix);
        return nextPrefix + "00000";
    }

    private static String incrementPrefix(String prefix) throws SQLException {
        char first = prefix.charAt(0);
        char second = prefix.charAt(1);
    
        if (second < 'Z') {
            second++;
        } else {
            second = 'A';
    
            if (first < 'Z') {
                first++;
            } else {
                throw new SQLException("No stock numbers remaining.");
            }
        }
    
        return "" + first + second;
    }

    public static String getStockNum(Connection connection, String manufacturer, String modelNumber) throws SQLException{
        String query = """
                SELECT stock_num
                FROM PRODUCTS 
                WHERE manufacturer = ? AND model_number = ? 
                """;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, manufacturer);
            statement.setString(2, modelNumber);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("stock_num");
                } else {
                    throw new SQLException("No stock_num found with manufacturer: " + manufacturer + " and model number: " + modelNumber);
                }
            }
        }
    }

    public static int setReplenishment(Connection connection, String stockNum, int newReplenishment) throws SQLException {
        String query = """
                UPDATE PRODUCTS
                SET replenishment = ?
                WHERE stock_num = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, newReplenishment);
            statement.setString(2, stockNum);

            return statement.executeUpdate();
        }
    }

    public static int getReplenishment(Connection connection, String stockNum) throws SQLException {
        String query = """
                SELECT replenishment
                FROM PRODUCTS
                WHERE stock_num = ?
                """;
    
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, stockNum);
    
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("replenishment");
                } else {
                    throw new SQLException("No product found with stock_num: " + stockNum);
                }
            }
        }
    }

    public static int setQuantity(Connection connection, String stockNum, int newQuantity) throws SQLException {
        String query = """
                UPDATE PRODUCTS
                SET quantity = ?
                WHERE stock_num = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, newQuantity);
            statement.setString(2, stockNum);

            return statement.executeUpdate();
        }
    }
    
    public static int getQuantity(Connection connection, String stockNum) throws SQLException {
        String query = """
                SELECT quantity
                FROM PRODUCTS
                WHERE stock_num = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, stockNum);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("quantity");
                } else {
                    throw new SQLException("No product found with stock number: " + stockNum);
                }
            }
        }
    }

    public static void printProducts(Connection connection) throws SQLException {
        String query = """
            SELECT *
            FROM PRODUCTS
            ORDER BY stock_num
        """;

        try (
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query)
        ) {
            System.out.println("PRODUCTS:");
            System.out.printf(
                "%-10s %-10s %-15s %-15s %-10s %-5s %-5s %-15s%n",
                "STOCK_NUM",
                "LOCATION",
                "MANUFACTURER",
                "MODEL_NUMBER",
                "QUANTITY",
                "MIN",
                "MAX",
                "REPLENISHMENT"
            );
            while (resultSet.next()) {
                System.out.printf(
                    "%-10s %-10s %-15s %-15s %-10d %-5d %-5d %-15d%n",
                    resultSet.getString("stock_num"),
                    resultSet.getString("location_id"),
                    resultSet.getString("manufacturer"),
                    resultSet.getString("model_number"),
                    resultSet.getInt("quantity"),
                    resultSet.getInt("min_stock_level"),
                    resultSet.getInt("max_stock_level"),
                    resultSet.getInt("replenishment")
                );
            }
        } catch (Exception e) {
            System.out.println("ERROR: selection from products failed.");
            System.out.println(e);
        }
    }

}