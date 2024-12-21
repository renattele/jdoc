package jdoc.recent.data;

import jdoc.recent.domain.RecentDocumentsRepository;
import jdoc.core.domain.Settings;

import java.util.ArrayList;
import java.util.List;

public class RecentDocumentsRepositoryImpl implements RecentDocumentsRepository {
    private static final String RECENT_KEY = "recent";
    private final Settings settings;

    public RecentDocumentsRepositoryImpl(Settings settings) {
        this.settings = settings;
    }

    @Override
    public void addRecent(String url) {
        var recent = new ArrayList<>(getRecent());
        recent.remove(url);
        recent.add(url);
        settings.put(RECENT_KEY, recent);
    }

    @Override
    public void deleteRecent(String url) {
        var recent = new ArrayList<>(getRecent());
        recent.remove(url);
        settings.put(RECENT_KEY, recent);
    }

    @Override
    public List<String> getRecent() {
        return settings.get(RECENT_KEY, new ArrayList<>());
    }
}
