package sbr.exaples.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.Test;

/**
 * @author senyasdr
 */
public class CursorTest {

    @Test
    void clientCursor() throws SQLException {
        try (Connection conn = Main.getDefaultConnection();
             PreparedStatement st = conn.prepareStatement("SELECT generate_series(1, 5) as id"))
        {

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    System.out.println(rs.getInt("id"));
                }
            }
        }
    }

    @Test
    void serverCursor() throws SQLException {
        try (Connection conn = Main.getDefaultConnection();
             PreparedStatement st = conn.prepareStatement("SELECT generate_series(1, 5) as id"))
        {
            conn.setAutoCommit(false);
            st.setFetchSize(2);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    System.out.println(rs.getInt("id"));
                }
            }


        }
    }

    @Test
    void cursorScrollable() throws SQLException {

        try (Connection conn = Main.getDefaultConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement st =
                    conn.prepareStatement("SELECT generate_series(1, 5) as id", ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_READ_ONLY);
            st.setFetchSize(2);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    System.out.println(rs.getInt("id"));
                }
                while (rs.previous()) {
                    System.out.println(rs.getInt("id"));
                }
            }
        }
    }

    @Test
    void trueServerCursor() throws SQLException {
        try (Connection conn = Main.getDefaultConnection()) {
            conn.setAutoCommit(false);

            Statement stmt = conn.createStatement();
            stmt.execute("CREATE OR REPLACE FUNCTION reffunc(ref refcursor) RETURNS refcursor AS '\n" +
                    "    BEGIN\n" +
                    "        OPEN ref SCROLL FOR SELECT generate_series(1, 50);\n" +
                    "        RETURN ref;\n" +
                    "    END;\n" +
                    "' LANGUAGE plpgsql;");
            stmt.execute("SELECT reffunc('mycur');");
            stmt.execute("FETCH FORWARD 3 FROM mycur;");
            ResultSet results = stmt.getResultSet();
            System.out.println("Print results");
            System.out.println("--------------------");
            while (results.next()) {
                System.out.println("- " + results.getInt(1));
            }
            results.close();
            stmt.execute("FETCH PRIOR FROM mycur;");

            results = stmt.getResultSet();
            System.out.println("--------------------");
            while (results.next()) {
                System.out.println("- " + results.getInt(1));
            }
            results.close();
            stmt.close();
            conn.commit();
        }
    }
}
