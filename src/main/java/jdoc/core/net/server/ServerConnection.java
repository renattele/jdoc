package jdoc.core.net.server;

import io.reactivex.rxjava3.core.Flowable;
import jdoc.core.net.protocol.Message;

import java.util.List;

public interface ServerConnection extends AutoCloseable {
    Flowable<Message> messages();

    Flowable<List<String>> clients();

    Flowable<String> newClients();

    void broadcast(Message message);

    void send(String addr, Message message);
}
