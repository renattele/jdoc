package jdoc.recent.data;

import jdoc.recent.domain.RecentDocument;
import jdoc.recent.domain.RecentDocumentsRepository;
import jdoc.core.domain.Settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecentDocumentsRepositoryImpl implements RecentDocumentsRepository {
    private static final String RECENT_KEY = "recent";
    private final Settings settings;

    public RecentDocumentsRepositoryImpl(Settings settings) {
        this.settings = settings;
    }

    @Override
    public void addRecent(RecentDocument recentDocument) {
        var recent = new ArrayList<>(getRecent());
        recent.removeIf(document -> document.equals(recentDocument));
        recent.add(recentDocument);
        settings.put(RECENT_KEY, recent);
    }

    @Override
    public void deleteRecent(String url) {
        var recent = new ArrayList<>(getRecent());
        recent.removeIf(document -> document.remoteUrl().equals(url));
        settings.put(RECENT_KEY, recent);
    }

    @Override
    public List<RecentDocument> getRecent() {
        var result = settings.get(RECENT_KEY, new RecentDocument[] {});
        return Arrays.asList(result);
    }
}
