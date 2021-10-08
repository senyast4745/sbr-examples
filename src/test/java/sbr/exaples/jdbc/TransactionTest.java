package sbr.exaples.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Savepoint;

import org.junit.jupiter.api.Test;
import sbr.exaples.jdbc.Main;

/**
 * @author senyasdr
 */
public class TransactionTest extends AbstractWithTableTest {

    @Test
    void executeWithoutTransaction() throws SQLException {
        try (Connection conn = Main.getDefaultConnection()) {
            PreparedStatement st = conn.prepareStatement("INSERT INTO users(login) VALUES ('test1')");
            st.executeUpdate();
            if (true) {
                throw new RuntimeException("Oops");
            }
            st = conn.prepareStatement("INSERT INTO users(login) VALUES ('test2')");
            st.executeUpdate();
        }
    }

    @Test
    void executeWithTransaction() throws SQLException {
        try (Connection conn = Main.getDefaultConnection()) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement st = conn.prepareStatement("INSERT INTO users(login) VALUES ('test1')");
                st.executeUpdate();
                if (true) {
                    throw new RuntimeException("Oops");
                }
                st = conn.prepareStatement("INSERT INTO users(login) VALUES ('test2')");
                st.executeUpdate();
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
            }
            conn.setAutoCommit(true);
        }
    }

    @Test
    void executeWithTransactionAndSavepoint() throws SQLException {
        try (Connection conn = Main.getDefaultConnection()) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement st = conn.prepareStatement("INSERT INTO users(login) VALUES ('test1')");
                st.executeUpdate();
                st.close();
                Savepoint sv1 = conn.setSavepoint("firstSv");
                try {
                    st = conn.prepareStatement("INSERT INTO users(login) VALUES ('test2')");
                    st.executeUpdate();
                    st.close();
                    if(true) {
                        throw new RuntimeException("Oops");
                    }
                    st = conn.prepareStatement("INSERT INTO users(login) VALUES ('test4')");
                    st.executeUpdate();
                } catch (Exception e) {
                    System.out.println("Error " + e.getMessage());
                    conn.rollback(sv1);
                }
                conn.releaseSavepoint(sv1);
                conn.commit();
                st.close();
            } catch (Exception e) {
                conn.rollback();
            }
            conn.setAutoCommit(true);
        }
    }

}
