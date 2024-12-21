package jdoc.document.data.user;

import com.github.javafaker.Faker;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.processors.ReplayProcessor;
import jdoc.document.domain.User;
import jdoc.document.domain.UserRepository;
import jdoc.document.domain.change.user.RenameUserChange;
import jdoc.document.domain.change.user.UserChange;
import jdoc.document.domain.source.DataSource;
import jdoc.document.domain.source.DataSourceOrchestrator;
import jdoc.document.domain.source.RemoteDataSource;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserRepositoryImpl implements UserRepository {
    private final List<User> users = new ArrayList<>();
    private final ReplayProcessor<List<User>> userListChanges = ReplayProcessor.create();
    private DataSourceOrchestrator<UserChange> orchestrator = null;
    private final DataSource<UserChange> listDataSource = new DataSource<UserChange>() {
        private final ReplayProcessor<UserChange> userChanges = ReplayProcessor.create();
        @Override
        public void close() {
        }

        @Override
        public void apply(UserChange change) {
            change.apply(users);
            userListChanges.onNext(users);
            userChanges.onNext(change);
        }

        @Override
        public Flowable<UserChange> changes() {
            return userChanges;
        }
    };

    @SuppressWarnings("unchecked")
    public UserRepositoryImpl(DataSourceOrchestrator.Factory orchestratorFactory, String url, RemoteDataSource.Factory<UserChange> remoteDataSourceFactory) {
        try {
            orchestrator = orchestratorFactory.create(
                    remoteDataSourceFactory.create(url),
                    listDataSource
            );
            var faker = new Faker();
            setName(faker.name().username());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Flowable<List<User>> users() {
        return userListChanges;
    }

    @Override
    public void setName(String username) {
        listDataSource.apply(new RenameUserChange(new User(username, getId())));
    }

    private final String id = UUID.randomUUID().toString();

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void close() throws Exception {
        if (orchestrator != null) orchestrator.close();
    }

    @AllArgsConstructor
    public static class Factory implements UserRepository.Factory {
        private final DataSourceOrchestrator.Factory orchestratorFactory;
        private final RemoteDataSource.Factory<UserChange> remoteDataSourceFactory;

        @Override
        public UserRepository create(String url) {
            return new UserRepositoryImpl(orchestratorFactory, url, remoteDataSourceFactory);
        }
    }
}
