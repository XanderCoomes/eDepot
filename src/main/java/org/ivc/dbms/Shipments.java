package org.ivc.dbms;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Shipments {
    public static void addShipmentItem(Connection connection, String noticeId, String manufacturer, String modelNumber, int quantity) throws SQLException {
        String query = """
                INSERT INTO SHIPITEMS (
                    manufacturer,
                    model_number,
                    notice_id,
                    quantity
                )
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, manufacturer);
            statement.setString(2, modelNumber);
            statement.setString(3, noticeId);
            statement.setInt(4, quantity);

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted != 1) {
                throw new SQLException(
                        "Shipment item insert failed for notice_id: "
                        + noticeId
                        + ", manufacturer: "
                        + manufacturer
                        + ", model_number: "
                        + modelNumber
                );
            }
        }
    }
    
    public static void addShippingNotice(Connection connection, String noticeId, String carrier, String status) throws SQLException {
        String query = """
                INSERT INTO SHIPNOTICES (
                    notice_id,
                    carrier,
                    shipping_status
                )
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, noticeId);
            statement.setString(2, carrier);
            statement.setString(3, status);

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted != 1) {
                throw new SQLException("Shipping notice insert failed for notice_id: " + noticeId);
            }
        }
    }

    //precondition: products are already in the depot
    public static void replenish(Connection connection, String noticeID, String manufacturer, String modelNumber, int quantity) throws SQLException{
        String stockNum = Products.getStockNum(connection, manufacturer, modelNumber);
        Products.setReplenishment(connection, stockNum, quantity);
    }
}