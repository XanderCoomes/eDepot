package org.ivc.dbms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UtilsDAO {
    public static void resetDatabase(Connection connection) throws SQLException {
        String deleteShipItems = "DELETE FROM SHIPITEMS";
        String deleteShipNotices = "DELETE FROM SHIPNOTICES";
        String deleteProducts = "DELETE FROM PRODUCTS";

        boolean oldAutoCommit = connection.getAutoCommit();

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement ps = connection.prepareStatement(deleteShipItems)) {
                ps.executeUpdate();
            }

            try (PreparedStatement ps = connection.prepareStatement(deleteShipNotices)) {
                ps.executeUpdate();
            }

            try (PreparedStatement ps = connection.prepareStatement(deleteProducts)) {
                ps.executeUpdate();
            }

            connection.commit();

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(oldAutoCommit);
        }
    }

}
