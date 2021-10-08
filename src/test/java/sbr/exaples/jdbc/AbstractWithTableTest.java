package sbr.exaples.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * @author senyasdr
 */
public abstract class AbstractWithTableTest {

    @BeforeEach
    void setUp() throws SQLException {
        try (Connection conn = Main.getDefaultConnection()) {
            conn.prepareStatement(getSetUpSQL())
                    .execute();
        }
    }

    @AfterEach
    void showResults() throws SQLException {
        System.out.println("Table content");
        System.out.println("----------------------------");
        try (Connection conn = Main.getDefaultConnection();
             PreparedStatement st = conn.prepareStatement("SELECT * FROM users")) {
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                mapResult(rs);
            }
            conn.prepareStatement("DROP TABLE IF EXISTS users").execute();
        }
    }

    protected void mapResult(ResultSet rs) throws SQLException {
        System.out.println("Id: " + rs.getInt("id") + ", login: " + rs.getString("login"));
    }

    protected String getSetUpSQL(){
        return "CREATE TABLE IF NOT EXISTS users (id BIGSERIAL PRIMARY KEY, login VARCHAR UNIQUE)";
    }
}
