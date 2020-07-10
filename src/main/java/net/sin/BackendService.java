package net.sin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.UrlEscapers;
import net.sin.model.ProductResult;
import net.sin.model.ShopResult;
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
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.emptySet;

@Service
public class BackendService {
    private static final Logger log = LoggerFactory.getLogger(BackendService.class);
    @Autowired
    private HttpClient httpClient;
    @Autowired
    private ObjectMapper objectMapper;
    @Value("${backend-uri}")
    private String backendUri;

    private HttpRequest.Builder initRequest(String path) {
        return HttpRequest.newBuilder(URI.create(backendUri + path));
    }

    public Set<String> getSuggestion(String term) {
        String body = get("/tokopedia/suggest/" + term);
        try {
            return objectMapper.readValue(body, objectMapper.getTypeFactory().constructCollectionType(HashSet.class, String.class));
        } catch (Exception e) {
            log.error("failed get suggestion, got body: " + body, e);
            return emptySet();
        }
    }

    public Set<ShopResult> search(String term) {
        String body = get("/tokopedia/search/" + term);
        try {
            return objectMapper.readValue(body, objectMapper.getTypeFactory().constructCollectionType(HashSet.class, ShopResult.class));
        } catch (Exception e) {
            log.error("failed get suggestion, got body: " + body, e);
            return emptySet();
        }
    }

    public Set<ProductResult> searchPerStore(long storeId, String term) {
        String body = get("/tokopedia/search/" + storeId + "/" + term);
        try {
            return objectMapper.readValue(body, objectMapper.getTypeFactory().constructCollectionType(HashSet.class, ProductResult.class));
        } catch (Exception e) {
            log.error("failed get suggestion, got body: " + body, e);
            return emptySet();
        }
    }

    private String get(String path) {
        path = UrlEscapers.urlFragmentEscaper().escape(path);
        log.info("get path: {}", path);
        HttpResponse<String> response = httpClient.sendAsync(initRequest(path).build(), BodyHandlers.ofString()).completeOnTimeout(null, 5, TimeUnit.SECONDS).join();
        if (response == null) return "";
        String body = response.body();
        if (body.isEmpty()) return "";
        return body;
    }
}
