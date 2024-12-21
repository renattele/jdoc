package jdoc.core.net.client;

import io.reactivex.rxjava3.core.Flowable;
import jdoc.core.net.protocol.Message;

public interface ClientConnection extends AutoCloseable {
    void send(Message message);

    Flowable<Message> incoming();

    @Override
    String toString();
}
