package org.ivc.dbms;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ShipmentDAO {
    public static void addShipmentItems(Connection connection, String noticeId, List<Item> shipmentItems) throws SQLException {
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
                statement.setString(2, noticeId);
                statement.setInt(3, item.getQuantity());
                statement.addBatch();
            }
    
            int[] rowsInserted = statement.executeBatch();
    
            for (int rows : rowsInserted) {
                if (rows != 1) {
                    throw new SQLException("One or more shipment item inserts failed for notice_id: " + noticeId);
                }
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
    public static void setReplenishments(Connection connection, List<Item> shipmentItems) throws SQLException{
        for (Item shipmentItem: shipmentItems){
            ProductDAO.setReplenishment(connection, shipmentItem.getStockNum(), shipmentItem.getQuantity());        }
    }


    }
