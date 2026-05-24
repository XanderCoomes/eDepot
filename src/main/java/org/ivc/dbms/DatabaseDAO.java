package org.ivc.dbms;
import java.sql.SQLException;
import java.util.Properties;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.pool.OracleDataSource;


//NOTE BEFORE EXECUTION: need to export the username & password
public class DatabaseDAO {
    private static final String DB_URL =
        "jdbc:oracle:thin:@edepot_tp?TNS_ADMIN=Wallet_eDepot";
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    public static OracleConnection getConnection() throws SQLException {
        Properties info = new Properties();

        info.put(OracleConnection.CONNECTION_PROPERTY_USER_NAME, DB_USER);
        info.put(OracleConnection.CONNECTION_PROPERTY_PASSWORD, DB_PASSWORD);
        info.put(OracleConnection.CONNECTION_PROPERTY_DEFAULT_ROW_PREFETCH, "20");

        OracleDataSource ods = new OracleDataSource();
        ods.setURL(DB_URL);
        ods.setConnectionProperties(info);

        return (OracleConnection) ods.getConnection();
    }
}
