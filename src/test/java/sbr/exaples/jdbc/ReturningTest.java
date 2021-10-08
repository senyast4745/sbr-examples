package sbr.exaples.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sbr.exaples.jdbc.Main;

/**
 * @author senyasdr
 */
public class ReturningTest extends AbstractWithTableTest {

    @Test
    void returning() throws SQLException {
        try (Connection cn = Main.getDefaultConnection()) {

            try (
                    PreparedStatement st = cn
                            .prepareStatement("INSERT INTO users(login) VALUES ('test1') RETURNING id,login");
                    ResultSet rs = st.executeQuery())
            {
                System.out.println("Returning");
                System.out.println("--------------------");
                while (rs.next()) {
                    Assertions.assertEquals("test1", rs.getString("login"));
                    System.out.println("Id: " + rs.getInt("id") + ", login: " + rs.getString("login"));
                }
                System.out.println("");
            }
        }
    }
}
