package jdoc.recent.domain;

import java.util.List;

public interface RecentDocumentsRepository {
    void addRecent(RecentDocument document);
    void deleteRecent(String url);
    List<RecentDocument> getRecent();
}
