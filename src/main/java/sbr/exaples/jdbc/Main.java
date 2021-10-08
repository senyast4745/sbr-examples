package sbr.exaples.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author senyasdr
 */
public class Main {

    private Main(){}

    public static Connection getDefaultConnection() throws SQLException {
        Properties prop = new Properties();
        prop.put("user", "postgres");
        prop.put("password", "password");
        prop.put("preferQueryMode", "extendedForPrepared");
        prop.put("targetServerType", "primary");
        return DriverManager.getConnection(
                "jdbc:postgresql://127.0.0.1:5432/postgres", prop);
    }

}


