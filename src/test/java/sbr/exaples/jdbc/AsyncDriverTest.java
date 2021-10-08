package sbr.exaples.jdbc;

import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * @author senyasdr
 */
class AsyncDriverTest {

    @Test
    void executeQuery() throws IOException {
        try (AsyncDriver driver = new AsyncDriver("postgres", "postgres", 5432)) {
            for (int i = 0; i < 2; i++) {
                driver.executeQuery(String.format("SELECT pg_sleep(5);SELECT %d;", i));
            }
            driver.getResult();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}