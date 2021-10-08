package sbr.exaples.jdbc;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

/**
 * @author senyasdr
 */
class SimpleApp2Test {

    @Test
    void executeN10Request() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/hello"))
                .build();
        List<CompletableFuture> req = new ArrayList<>();
        System.out.println("Start test time: " + Instant.now());
        long startTimeMillis = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            req.add(client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenApply(b -> "Test number " + finalI + ", ts " + Instant.now() + ": " + b + " ")
                    .thenAccept(System.out::println));
        }
        CompletableFuture.allOf(req.toArray(new CompletableFuture[0])).join();
        System.out.println("One test time in millis : " + (System.currentTimeMillis() - startTimeMillis) / (10));
        System.out.println("End test time: " + Instant.now());
    }

    @Test
    void executeN500Request() throws InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/hello"))
                .build();
        List<CompletableFuture<?>> req = new ArrayList<>();
        AtomicInteger failCount = new AtomicInteger(0);
        System.out.println("Start first test time: " + Instant.now());
        long startTimeMillis = System.currentTimeMillis();
        for (int i = 0; i < 50; i++) {
            req.add(client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(r -> {
                        if (r.statusCode() != 200) {
                            failCount.incrementAndGet();
                        }
                    }));
        }
        CompletableFuture.allOf(req.toArray(new CompletableFuture[0])).join();
        System.out.println("First test time in millis : " + (System.currentTimeMillis() - startTimeMillis) / (50));
        System.out.println("End first test time: " + Instant.now());
        System.out.println("Fail count in first test: " + failCount.get());

        Thread.sleep(100);

        AtomicInteger failCount2 = new AtomicInteger(0);
        System.out.println("Start second test time: " + Instant.now());
        startTimeMillis = System.currentTimeMillis();
        for (int i = 0; i < 50; i++) {
            req.add(client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(r -> {
                        if (r.statusCode() != 200) {
                            failCount2.incrementAndGet();
                        }
                    }));
        }
        CompletableFuture.allOf(req.toArray(new CompletableFuture[0])).join();
        System.out.println("Second test time in millis : " + (System.currentTimeMillis() - startTimeMillis) / (50));
        System.out.println("End test time: " + Instant.now());
        System.out.println("Second fail count " + failCount2.get());
    }


    @Test
    void executeN1000Request() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/hello"))
                .build();
        List<CompletableFuture> req = new ArrayList<>();
        AtomicInteger failCount = new AtomicInteger(0);
        for (int i = 0; i < 1000; i++) {
            int finalI = i;
            req.add(client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(r -> {
                        if (r.statusCode() != 200) {
                            failCount.incrementAndGet();
                        }
                        return r;
                    })
                    .thenApply(HttpResponse::body)
                    .thenApply(b -> "Test number " + finalI + ", ts " + Instant.now() + ": " + b + " ")
                    .thenAccept(System.out::println));
        }

        CompletableFuture.allOf(req.toArray(new CompletableFuture[0])).join();
        System.out.println("Fail count " + failCount.get());
    }
}