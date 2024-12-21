package jdoc.recent.domain;

import java.util.List;

public interface RecentDocumentsRepository {
    void addRecent(String url);
    void deleteRecent(String url);
    List<String> getRecent();
}
