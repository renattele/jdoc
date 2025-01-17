package jdoc.user.domain;

import jdoc.core.domain.source.DataSource;
import jdoc.core.domain.source.DataSourceOrchestrator;
import jdoc.user.domain.change.UserChange;

import java.util.List;

public interface UserList extends DataSourceOrchestrator<UserChange> {
    static UserList wrap(DataSourceOrchestrator<UserChange> orchestrator) {
        return new UserList() {
            @Override
            public void addSource(DataSource<UserChange> source) {
                orchestrator.addSource(source);
            }

            @Override
            public void removeSource(DataSource<UserChange> source) {
                orchestrator.removeSource(source);
            }

            @Override
            public List<DataSource<UserChange>> originalSources() {
                return orchestrator.originalSources();
            }

            @Override
            public void close() throws Exception {
                orchestrator.close();
            }
        };
    }
}
