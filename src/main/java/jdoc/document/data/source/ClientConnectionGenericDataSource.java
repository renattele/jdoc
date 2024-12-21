package jdoc.document.data.source;

import io.reactivex.rxjava3.core.Flowable;
import jdoc.core.domain.Serializer;
import jdoc.core.net.client.ClientConnection;
import jdoc.core.net.client.ClientConnectionDataSource;
import jdoc.core.net.protocol.Message;
import jdoc.document.domain.change.Change;
import jdoc.document.domain.source.RemoteDataSource;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ClientConnectionGenericDataSource<CHANGE extends Change<?, CHANGE>> implements RemoteDataSource<CHANGE> {
    private final ClientConnection connection;
    private final Serializer serializer;
    private final Flowable<CHANGE> changesCached;
    private final int messageType;
    private CHANGE reducedChange;

    public ClientConnectionGenericDataSource(
            ClientConnection connection,
            Serializer serializer,
            int messageType,
            int syncRequestType,
            int syncResponseType,
            Class<CHANGE> clazz
    ) {
        this.connection = connection;
        this.serializer = serializer;
        this.messageType = messageType;
        new Thread(() -> send(syncRequestType)).start();
        this.changesCached = connection.incoming().mapOptional(message -> {
            System.out.println("INCOMING: " + message);
            if (message.type() == messageType) {
                return Optional.of(serializer.fromString(message.dataString(), clazz));
            } else {
                if (message.type() == syncRequestType) {
                    System.out.println("SYNC REQUEST: " + message + ". CHANGE: " + reducedChange);
                    send(syncResponseType, reducedChange);
                } else if (message.type() == syncResponseType) {
                    System.out.println("SYNC RESPONSE: " + message);
                    if (message.dataString().isEmpty()) return Optional.empty();
                    return Optional.ofNullable(serializer.fromString(message.dataString(), clazz));
                }
                return Optional.empty();
            }
        }).map(change -> {
            updateReducedChange(change);
            return change;
        }).cache();
    }

    @Override
    public void apply(CHANGE change) {
        updateReducedChange(change);
        send(messageType, change);
    }

    private synchronized void updateReducedChange(CHANGE change) {
        if (reducedChange == null) {
            reducedChange = change.reduce(List.of(change));
        } else {
            reducedChange = change.reduce(List.of(reducedChange, change));
        }
    }

    private void send(int messageType, CHANGE change) {
        connection.send(new Message(messageType, serializer.toString(change)));
    }

    private void send(int messageType) {
        connection.send(new Message(messageType, ""));
    }

    @Override
    public Flowable<CHANGE> changes() {
        return changesCached;
    }

    @Override
    public boolean populatesChanges() {
        return false;
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }

    @AllArgsConstructor
    public static class Factory<CHANGE extends Change<?, CHANGE>> implements RemoteDataSource.Factory<CHANGE> {
        private final Serializer serializer;
        private final ClientConnectionDataSource clientConnectionDataSource;
        private final int messageType;
        private final int syncRequestType;
        private final int syncResponseType;
        private final Class<CHANGE> clazz;

        @Override
        public RemoteDataSource<CHANGE> create(String url) throws IOException {
            return new ClientConnectionGenericDataSource<>(
                    clientConnectionDataSource.get(url),
                    serializer,
                    messageType,
                    syncRequestType,
                    syncResponseType,
                    clazz);
        }
    }
}
