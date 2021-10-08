package sbr.exaples.jdbc;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.servlet.ServletOutputStream;

import org.junit.jupiter.api.Test;

/**
 * @author senyasdr
 */
class SimpleAppTest {

    @Test
    void executeRequest() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/hello"))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(b -> b + " " + Instant.now())
                .thenAccept(System.out::println)
                .join();

    }

    @Test
    void executeNRequest() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/hello"))
                .build();
        List<CompletableFuture<?>> req = new ArrayList<>();
        System.out.println("Start test time: "  + Instant.now());
        long startTimeMillis = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            req.add(client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenApply(b -> "Test number " + finalI + ", ts " + Instant.now() + ": " + b + " ")
                    .thenAccept(System.out::println));
        }
        CompletableFuture.allOf(req.toArray(new CompletableFuture[0])).join();
        System.out.println("One test time in millis : " + (System.currentTimeMillis() - startTimeMillis) / 10);
        System.out.println("End test time: "  + Instant.now());
    }
}