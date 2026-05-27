package org.ivc.dbms;
import oracle.jdbc.OracleConnection;

public class Main {
    public static void main(String[] args) {
        try (OracleConnection connection = DatabaseDAO.getConnection()) {
            Server s = new Server(8001);
            // UtilsDAO.resetDatabase(connection);
            // ProductLoader.loadProducts(connection, "data/StarterData.xlsx");
            // ExternalWorld.runInterface(connection);
        } catch (Exception e) {
            System.out.println("ERROR:");
            System.out.println(e);
        }
        System.exit(0);
    }
}
