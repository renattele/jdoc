package jdoc.domain;

import io.reactivex.rxjava3.core.Flowable;

public interface Client {
    void send(Message operation);

    Flowable<Message> incoming();

    @Override
    String toString();
}
