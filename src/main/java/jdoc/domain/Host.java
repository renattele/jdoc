package jdoc.domain;

import io.reactivex.rxjava3.core.Flowable;

import java.util.List;

public interface Host {
    Flowable<Message> messages();
    Flowable<List<String>> clients();
    void broadcast(Message message);
}
