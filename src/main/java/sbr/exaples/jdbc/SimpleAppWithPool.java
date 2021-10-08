package sbr.exaples.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import static spark.Spark.exception;
import static spark.Spark.get;

/**
 * @author senyasdr
 */
public class SimpleAppWithPool {

    private static final HikariDataSource dataSource = getDataSource();

    public static void main(String[] args) {
        get("/hello", (req, res) -> {
            StringBuilder resBody = new StringBuilder();
            try (Connection cn = dataSource.getConnection()) {

                Statement sleepSt = cn.createStatement();
                sleepSt.execute("SELECT pg_sleep(0.05);");
                sleepSt.close();

                PreparedStatement st;
                st = cn.prepareStatement("SELECT generate_series(0, 1000) as id");
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        resBody.append(rs.getInt("id"));
                        resBody.append(" ");
                    }
                }
                st.close();
            }
            return resBody.toString();
        });

        exception(Exception.class, (exception, request, response) -> exception.printStackTrace());
    }

    public static HikariDataSource getDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/postgres");
        config.setUsername("postgres");
        config.setPassword("password");
        config.setMaximumPoolSize(50);
        return new HikariDataSource(config);
    }
}
