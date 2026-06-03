package org.ivc.dbms;
import oracle.jdbc.OracleConnection;

public class Main {
    public static void main(String[] args) {
        try (OracleConnection connection = DatabaseDAO.getConnection()) {
            Server server = new Server(8001, connection);
            server.start();
            UtilsDAO.resetDatabase(connection);
            ProductLoader.loadProducts(connection, "data/StarterData.xlsx");
            Thread.sleep(1000); // wait 1 second
            // ExternalWorld.runInterface(connection);

            Tester.test(connection); 
        } catch (Exception e) {
            System.out.println("ERROR:");
            System.out.println(e);
        }
        System.exit(0);
    }
}
