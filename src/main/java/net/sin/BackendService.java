package net.sin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.emptyList;

@Service
public class BackendService {
    private static final Logger log = LoggerFactory.getLogger(BackendService.class);
    private final ConcurrentLinkedDeque<CompletableFuture<HttpResponse<String>>> cache = new ConcurrentLinkedDeque<>();
    @Autowired
    private HttpClient httpClient;
    @Autowired
    private ObjectMapper objectMapper;
    @Value("${backend-uri}")
    private String backendUri;

    private HttpRequest.Builder initRequest(String path) {
        return HttpRequest.newBuilder(URI.create(backendUri + path));
    }

    public List<String> getSuggestion(String term) {
        CompletableFuture<HttpResponse<String>> future = httpClient.sendAsync(initRequest("/tokopedia/suggest/" + term).build(), BodyHandlers.ofString()).completeOnTimeout(null, 5, TimeUnit.SECONDS);
        cache.add(future);
        try {
            HttpResponse<String> response = future.join();
            if (response == null) return emptyList();
            String body = response.body();
            if (body.isEmpty()) return emptyList();
            try {
                return objectMapper.readValue(body, objectMapper.getTypeFactory().constructCollectionType(Collection.class, String.class));
            } catch (JsonProcessingException e) {
                log.error("failed get suggestion", e);
                return emptyList();
            }
        } finally {
            cache.remove(future);
        }
    }
}
