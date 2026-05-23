package org.ivc.dbms;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Products {
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

    public static int getMinStockLevel(Connection connection, String stockNum) throws SQLException {
        String query = """
                SELECT min_stock_level
                FROM PRODUCTS
                WHERE stock_num = ?
                """;
    
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, stockNum);
    
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("min_stock_level");
                } else {
                    throw new SQLException("No product found with stock_num: " + stockNum);
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