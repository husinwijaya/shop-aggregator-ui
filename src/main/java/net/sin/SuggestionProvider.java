package net.sin;

import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;

public class SuggestionProvider {
    private final BackendService backendService;
    private final Map<String, List<String>> cache = new HashMap<>();
    private final BackendCallback callback = new BackendCallback();

    private SuggestionProvider(BackendService backendService) {
        this.backendService = backendService;
    }

    public static CallbackDataProvider<String, String> create(BackendService service) {
        SuggestionProvider callback = new SuggestionProvider(service);
        return new CallbackDataProvider<>(callback.callback, callback.callback);
    }

    private class BackendCallback implements FetchCallback<String, String>, CountCallback<String, String> {

        @Override
        public int count(Query<String, String> query) {
            if (query.getFilter().isEmpty()) return 0;
            String term = query.getFilter().get();
            List<String> suggestion = backendService.getSuggestion(term);
            if (suggestion.isEmpty()) suggestion = singletonList(term);
            cache.put(term, suggestion);
            return suggestion.size();
        }

        @Override
        public Stream<String> fetch(Query<String, String> query) {
            if (query.getFilter().isEmpty()) return Stream.empty();
            return cache.remove(query.getFilter().get()).stream().limit(query.getLimit()).skip(query.getOffset());
        }

    }

}
