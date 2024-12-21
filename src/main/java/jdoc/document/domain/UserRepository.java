package jdoc.document.domain;

import io.reactivex.rxjava3.core.Flowable;

import java.util.List;

public interface UserRepository extends AutoCloseable {
    Flowable<List<User>> users();
    void setName(String username);
    String getId();
    interface Factory {
        UserRepository create(String url);
    }
}
