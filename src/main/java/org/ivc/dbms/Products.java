package org.ivc.dbms;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Products {

    // Displays data from products table.
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

            while (resultSet.next()) {
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
            }

        } catch (Exception e) {
            System.out.println("ERROR: selection from products failed.");
            System.out.println(e);
        }
    }
}