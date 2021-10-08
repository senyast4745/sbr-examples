package sbr.exaples.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author senyasdr
 */

class MainTest {

    @Test
    void shouldGetJdbcConnection() throws SQLException {
        try (Connection conn = Main.getDefaultConnection()) {
            assertTrue(conn.isValid(1));
            assertFalse(conn.isClosed());
        }
    }

    @Test
    void simpleSelect() throws SQLException
    {
        try (Connection conn = Main.getDefaultConnection();
             Statement statement = conn.createStatement())
        {
            boolean hasResult = statement.execute("SELECT generate_series(0,1)");
            assertTrue(hasResult);
        }
    }

    @Test
    void simpleSelectSeq() throws SQLException
    {
        try (Connection conn = Main.getDefaultConnection();
             Statement statement = conn.createStatement())
        {
            boolean hasResult = statement.execute("SELECT 1; SELECT 2;");
            assertTrue(hasResult);
            try (ResultSet rs = statement.getResultSet()) {
                while (rs.next()) {
                    System.out.println(rs.getLong(1));
                }
            }
        }
    }

    @Test
    void simpleSelectUnion() throws SQLException
    {
        try (Connection conn = Main.getDefaultConnection();
             Statement statement = conn.createStatement())
        {
            boolean hasResult = statement.execute("SELECT 1 UNION SELECT 2;");
            assertTrue(hasResult);
            try (ResultSet rs = statement.getResultSet()) {
                while (rs.next()) {
                    System.out.println(rs.getLong(1));
                }
            }
        }
    }

    @Test
    void simpleSelectPseudoParallel() throws SQLException
    {
        try (Connection conn = Main.getDefaultConnection())
        {
            conn.setAutoCommit(false);
            List<CompletableFuture<Void>> f = new ArrayList<>();
            f.add(CompletableFuture.runAsync(() -> {
                try {
                    long start = System.currentTimeMillis();
                    Statement st1 = conn.createStatement();
                    st1.execute("SELECT pg_sleep(5); SELECT 1;");
                    st1.close();
                    System.out.println("done 1 time: " + (System.currentTimeMillis() - start));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }));
            f.add(CompletableFuture.runAsync(() -> {
                try {
                    long start = System.currentTimeMillis();
                    Statement st2 = conn.createStatement();
                    st2.execute("SELECT pg_sleep(5); SELECT 2;");
                    st2.close();
                    System.out.println("done 2 time: " + (System.currentTimeMillis() - start));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }));
            CompletableFuture.allOf(f.toArray(new CompletableFuture[0])).join();
            conn.commit();
        }
    }

    @Test
    void extendedSelect() throws SQLException
    {
        try (Connection conn = Main.getDefaultConnection();
             PreparedStatement statement = conn.prepareStatement("SELECT generate_series(0, ?) as id"))
        {
            statement.setInt(1, 1);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    System.out.println(rs.getLong("id"));
                }
            }
        }
    }

    @Test
    void extendedSelectCached() throws SQLException
    {
        try (Connection conn = Main.getDefaultConnection()) {
            PreparedStatement st = conn.prepareStatement("SELECT generate_series(0, ?) as id");
            org.postgresql.PGStatement pgst = st.unwrap(org.postgresql.PGStatement.class);
            pgst.setPrepareThreshold(3);
            for (int i = 1; i < 7; i++) {
                System.out.println("Attempt " + i);
                st.setInt(1, i);
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    System.out.println(rs.getLong("id"));
                }
                rs.close();
            }
            st.close();
        }
    }
}