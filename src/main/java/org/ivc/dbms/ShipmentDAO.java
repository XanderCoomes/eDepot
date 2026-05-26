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

    public static void addShippingNotice(Connection connection, String noticeID, String carrier) throws SQLException {
        String query = """
                INSERT INTO SHIPNOTICES (
                    notice_id,
                    carrier,
                    is_filled
                )
                VALUES (?, ?, 0)
                """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, noticeID);
            statement.setString(2, carrier);

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted != 1) {
                throw new SQLException("Shipping notice insert failed for notice_id: " + noticeID);
            }
        }
    }
    
    public static void processShipNotice(Connection connection, String noticeID, String carrier, List<Item> shipmentItems) throws SQLException{
        addShippingNotice(connection, noticeID, carrier);
        addShipmentItems(connection, noticeID, shipmentItems); 
        setReplenishments(connection, shipmentItems); 
    }

    public static void setReplenishments(Connection connection, List<Item> shipmentItems) throws SQLException{
        for (Item shipmentItem: shipmentItems){
            ProductDAO.setReplenishment(connection, shipmentItem.getStockNum(), shipmentItem.getQuantity());        }
    }

    public static void updateShipmentStatus(Connection connection, String noticeID) throws SQLException {
        String query = """
                UPDATE SHIPNOTICES
                SET is_filled = 1
                WHERE notice_id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, noticeID);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated != 1) {
                throw new SQLException("Shipment status update failed for notice_id: " + noticeID);
            }
        }
    }

    public static int getShipmentStatus(Connection connection, String noticeID) throws SQLException {
        String query = """
                SELECT is_filled
                FROM SHIPNOTICES
                WHERE notice_id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, noticeID);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("is_filled");
                } else {
                    throw new SQLException("No shipment notice found with notice_id: " + noticeID);
                }
            }
        }
    }

    public static void receiveShipment(Connection connection, String noticeID) throws SQLException {
        if(getShipmentStatus(connection, noticeID) == 1){
            return;
        }
        updateShipmentStatus(connection, noticeID);
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
            statement.setString(1, noticeID);
    
            int rowsUpdated = statement.executeUpdate();
    
            if (rowsUpdated == 0) {
                throw new SQLException("No shipment items found for notice_id: " + noticeID);
            }
        }
    }

    public static void printShipmentNotices(Connection connection) throws SQLException {
        String sql = """
            SELECT 
                sn.notice_id,
                sn.carrier,
                sn.is_filled,
                si.stock_num,
                si.quantity
            FROM SHIPNOTICES sn
            LEFT JOIN SHIPITEMS si
                ON sn.notice_id = si.notice_id
            ORDER BY sn.notice_id, si.stock_num
            """;
    
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
    
            String currentNoticeID = null;
            boolean hasAnyNotices = false;
    
            while (resultSet.next()) {
                hasAnyNotices = true;
    
                String noticeID = resultSet.getString("notice_id");
                String carrier = resultSet.getString("carrier");
                int isFilled = resultSet.getInt("is_filled");
    
                if (!noticeID.equals(currentNoticeID)) {
                    if (currentNoticeID != null) {
                        System.out.println();
                    }
    
                    currentNoticeID = noticeID;
    
                    System.out.println("==============================================");
                    System.out.println("NOTICE ID: " + noticeID);
                    System.out.println("CARRIER:   " + carrier);
                    System.out.println("STATUS:    " + (isFilled == 1 ? "Filled" : "Not Filled"));
                    System.out.println("----------------------------------------------");
                    System.out.printf("%-12s %10s%n", "STOCK NUM", "QUANTITY");
                    System.out.println("----------------------------------------------");
                }
    
                String stockNum = resultSet.getString("stock_num");
    
                if (stockNum != null) {
                    int quantity = resultSet.getInt("quantity");
    
                    System.out.printf("%-12s %10d%n", stockNum, quantity);
                } else {
                    System.out.println("No shipment items for this notice.");
                }
            }
    
            if (!hasAnyNotices) {
                System.out.println("No shipment notices found.");
            }
        }
    }
}
