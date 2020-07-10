package net.sin;

import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import static java.util.Collections.emptySet;

public class SuggestionProvider {
    private static final Logger log = LoggerFactory.getLogger(SuggestionProvider.class);
    private final BackendService backendService;
    private final Map<String, Collection<String>> cache = new HashMap<>();
    private final BackendCallback callback = new BackendCallback();

    private SuggestionProvider(BackendService backendService) {
        this.backendService = backendService;
    }

    public static SuggestionCallbackDataProvider create(BackendService service) {
        SuggestionProvider callback = new SuggestionProvider(service);
        return callback.new SuggestionCallbackDataProvider(callback.callback, callback.callback);
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Future<Collection<String>> current;

    private class BackendCallback implements FetchCallback<String, String>, CountCallback<String, String> {

        @Override
        public int count(Query<String, String> query) {
            if (query.getFilter().isEmpty()) return 0;
            String term = query.getFilter().get();
            current = executor.submit(() -> backendService.getSuggestion(term));
            Collection<String> suggestion = emptySet();
            try {
                suggestion = current.get();
            } catch (Exception e) {
                log.warn("skip suggestion because: " + e);
            }
            if (suggestion.isEmpty()) return 0;
            cache.put(term, suggestion);
            return suggestion.size();
        }

        @Override
        public Stream<String> fetch(Query<String, String> query) {
            if (query.getFilter().isEmpty()) return Stream.empty();
            return cache.remove(query.getFilter().get()).stream().limit(query.getLimit()).skip(query.getOffset());
        }

    }

    public class SuggestionCallbackDataProvider extends CallbackDataProvider<String, String> {

        private SuggestionCallbackDataProvider(FetchCallback<String, String> fetchCallback, CountCallback<String, String> countCallback) {
            super(fetchCallback, countCallback);
        }

        public void cancelCurrent() {
            log.info("cancelCurrent");
            try {
                current.cancel(true);
            } catch (Exception e) {
                log.warn("fail to cancel current operation: " + e);
            }
        }
    }
}
