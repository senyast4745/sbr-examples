package sbr.exaples.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.stop;

public class SimpleApp {

    public static void main(String[] args) throws SQLException {
        Connection cn = getDefaultConnection();
        get("/hello", (req, res) -> {
            StringBuilder resBody = new StringBuilder();

            Statement sleepSt = cn.createStatement();
            sleepSt.execute("SELECT pg_sleep(0.05);");
            sleepSt.close();

            PreparedStatement st = cn.prepareStatement("SELECT generate_series(0, 1000) as id");
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    resBody.append(rs.getInt("id"));
                    resBody.append(" ");
                }
            }
            st.close();
            return resBody.toString();
        });


        exception(Exception.class, (exception, request, response) -> {
            exception.printStackTrace();
            response.status(500);
        });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                cn.close();
                stop();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }));
    }

    public static Connection getDefaultConnection() throws SQLException {
        Properties prop = new Properties();
        prop.put("user", "postgres");
        prop.put("password", "password");
        return DriverManager.getConnection(
                "jdbc:postgresql://127.0.0.1:5432/postgres", prop);
    }
}
