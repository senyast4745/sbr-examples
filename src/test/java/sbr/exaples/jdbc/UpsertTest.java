package sbr.exaples.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import sbr.exaples.jdbc.Main;

/**
 * @author senyasdr
 */
public class UpsertTest extends AbstractWithTableTest {

    @Test
    void upsert() throws SQLException {
        try (Connection cn = Main.getDefaultConnection()) {
            PreparedStatement st = cn.prepareStatement(
                    "INSERT INTO users(login, password) VALUES ('test1', 'change_me')");
            st.executeUpdate();
            st = cn.prepareStatement(
                    "INSERT INTO users(login, password)\n" +
                            "VALUES ('test1', 'change_me_again')\n" +
                            "ON CONFLICT (login) DO UPDATE SET password=excluded.password\n");
            st.executeUpdate();
            st.close();
        }
    }

    @Override
    protected String getSetUpSQL() {
        return "CREATE TABLE IF NOT EXISTS users (id BIGSERIAL PRIMARY KEY, login VARCHAR UNIQUE, password VARCHAR)";
    }

    @Override
    protected void mapResult(ResultSet rs) throws SQLException {
        System.out.println("Id: " + rs.getInt("id") + ", login: " + rs.getString("login") + ", password: " +
                rs.getString("password"));
    }
}
