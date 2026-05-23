package org.ivc.dbms;
import oracle.jdbc.OracleConnection;

public class Main {
    public static void main(String[] args) {
        try (OracleConnection connection = Database.getConnection()) {
            // ProductLoader.loadProducts(connection, "data/StarterData.xlsx"); //Load products into database
            Products.printProducts(connection);
            int qty = Products.getQuantity(connection, "AA00101");
            System.out.println(qty);
        } catch (Exception e) {
            System.out.println("ERROR:");
            System.out.println(e);
        }
        System.exit(0);
    }
}
