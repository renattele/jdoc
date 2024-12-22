package jdoc.user.data;

import jdoc.user.domain.UserList;
import jdoc.user.domain.UserRepository;
import jdoc.user.domain.change.UserChange;
import jdoc.core.domain.source.DataSourceOrchestrator;
import jdoc.core.domain.source.RemoteDataSource;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private final DataSourceOrchestrator.Factory orchestratorFactory;
    private final RemoteDataSource.Factory<UserChange> remoteDataSourceFactory;

    public UserRepositoryImpl(DataSourceOrchestrator.Factory orchestratorFactory, RemoteDataSource.Factory<UserChange> remoteDataSourceFactory) {
        this.orchestratorFactory = orchestratorFactory;
        this.remoteDataSourceFactory = remoteDataSourceFactory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public UserList getUsersByUrl(String url) {
        try {
            var remoteDataSource = remoteDataSourceFactory.create(url);
            return UserList.wrap(orchestratorFactory.create(
                    remoteDataSource
            ));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
