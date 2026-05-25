package org.ivc.dbms;
import oracle.jdbc.OracleConnection;

public class Main {
    public static void main(String[] args) {
        try (OracleConnection connection = DatabaseDAO.getConnection()) {
            Tester.test(connection); 
        
        } catch (Exception e) {
            System.out.println("ERROR:");
            System.out.println(e);
        }
        System.exit(0);
    }
}
