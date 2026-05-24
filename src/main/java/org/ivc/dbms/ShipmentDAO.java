package org.ivc.dbms;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ShipmentDAO {
    public static void addShipmentItems(Connection connection, String noticeID, List<Item> shipmentItems) throws SQLException {
        String query = """
                INSERT INTO SHIPITEMS (
                    stock_num,
                    notice_id,
                    quantity
                )
                VALUES (?, ?, ?)
                """;
    
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (Item item : shipmentItems) {
                statement.setString(1, item.getStockNum());
                statement.setString(2, noticeID);
                statement.setInt(3, item.getQuantity());
                statement.addBatch();
            }
    
            int[] rowsInserted = statement.executeBatch();
    
            for (int rows : rowsInserted) {
                if (rows != 1) {
                    throw new SQLException("One or more shipment item inserts failed for notice_id: " + noticeID);
                }
            }
        }
    }

    public static void addShippingNotice(Connection connection, String noticeID, String carrier, String status) throws SQLException {
        String query = """
                INSERT INTO SHIPNOTICES (
                    notice_id,
                    carrier,
                    shipping_status
                )
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, noticeID);
            statement.setString(2, carrier);
            statement.setString(3, status);

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted != 1) {
                throw new SQLException("Shipping notice insert failed for notice_id: " + noticeID);
            }
        }
    }
    
    public static void processShipNotice(Connection connection, String noticeID, String carrier, String status, List<Item> shipmentItems) throws SQLException{
        addShipmentItems(connection, noticeID, shipmentItems); 
        addShippingNotice(connection, noticeID, carrier, status);
        setReplenishments(connection, shipmentItems); 
    }
    
    //precondition: products are already in the depot
    public static void setReplenishments(Connection connection, List<Item> shipmentItems) throws SQLException{
        for (Item shipmentItem: shipmentItems){
            ProductDAO.setReplenishment(connection, shipmentItem.getStockNum(), shipmentItem.getQuantity());        }
    }

    public static void updateShipmentStatus(Connection connection, String noticeID, String status) throws SQLException {
        String query = """
                UPDATE SHIPNOTICES
                SET shipping_status = ?
                WHERE notice_id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, status);
            statement.setString(2, noticeID);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated != 1) {
                throw new SQLException("Shipment status update failed for notice_id: " + noticeID);
            }
        }
    }

    public static String getShipmentStatus(Connection connection, String noticeID) throws SQLException {
        String query = """
                SELECT shipping_status
                FROM SHIPNOTICES
                WHERE notice_id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, noticeID);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("shipping_status");
                } else {
                    throw new SQLException("No shipment notice found with notice_id: " + noticeID);
                }
            }
        }
    }

    public static void receiveShipment(Connection connection, String noticeId) throws SQLException {
        String query = """
                UPDATE PRODUCTS
                SET quantity = quantity + replenishment,
                    replenishment = 0
                WHERE stock_num IN (
                    SELECT stock_num
                    FROM SHIPITEMS
                    WHERE notice_id = ?
                )
                """;
    
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, noticeId);
    
            int rowsUpdated = statement.executeUpdate();
    
            if (rowsUpdated == 0) {
                throw new SQLException("No shipment items found for notice_id: " + noticeId);
            }
        }
    }









    }
