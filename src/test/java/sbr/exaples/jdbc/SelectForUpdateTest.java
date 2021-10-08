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
public class SelectForUpdateTest extends AbstractWithTableTest {

    @Test
    void selectForUpdate() throws SQLException {
        try (Connection cn = Main.getDefaultConnection()) {
            PreparedStatement st = cn.prepareStatement("INSERT INTO users(login) VALUES (?)");
            for (int i = 0; i < 3; i++) {
                st.setString(1, "test" + i);
                st.addBatch();
            }
            st.executeBatch();
            st.close();

            st = cn.prepareStatement("SELECT * FROM users WHERE login in ('test1', 'test2') FOR UPDATE",
                    ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    switch (rs.getString("login")) {
                        case "test1":
                            rs.updateString("login", "test123");
                            break;
                        case "test2":
                            rs.updateString("login", "Wow");
                            break;
                        default:
                            throw new UnsupportedOperationException();
                    }
                    rs.updateRow();
                }
            }
            st.close();
        }
    }
}
