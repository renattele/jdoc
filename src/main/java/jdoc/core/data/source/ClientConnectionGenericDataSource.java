package jdoc.core.data.source;

import io.reactivex.rxjava3.core.Flowable;
import jdoc.core.domain.Serializer;
import jdoc.core.net.client.ClientConnection;
import jdoc.core.net.client.ClientConnectionDataSource;
import jdoc.core.net.protocol.Message;
import jdoc.core.domain.change.Change;
import jdoc.core.domain.source.RemoteDataSource;
import jdoc.core.net.protocol.MessageType;
import jdoc.core.net.protocol.RequestToken;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Optional;

@Slf4j
public class ClientConnectionGenericDataSource<CHANGE extends Change<?, CHANGE>> implements RemoteDataSource<CHANGE> {
    private final ClientConnection connection;
    private final Serializer serializer;
    private final Flowable<CHANGE> changesCached;
    private final MessageType messageType;
    private CHANGE reducedChange;
    private RequestToken requestToken = RequestToken.EMPTY;

    public ClientConnectionGenericDataSource(
            ClientConnection connection,
            Serializer serializer,
            MessageType messageType,
            MessageType syncRequestType,
            MessageType syncResponseType,
            Class<CHANGE> clazz
    ) {
        this.connection = connection;
        this.serializer = serializer;
        this.messageType = messageType;
        new Thread(() -> {
            requestToken = RequestToken.generate();
            send(syncRequestType, requestToken);
        }).start();
        this.changesCached = connection.incoming().mapOptional(message -> {
            log.info("INCOMING: {}", message);
            if (message.type() == messageType) {
                return Optional.of(serializer.fromString(message.dataString(), clazz));
            } else {
                if (message.type() == syncRequestType) {
                    log.info("SYNC REQUEST: {}. CHANGE: {}", message, reducedChange);
                    send(syncResponseType, message.requestToken(), reducedChange);
                } else if (message.type() == syncResponseType) {
                    log.info("SYNC RESPONSE: {}", message);
                    if (message.requestToken().equals(requestToken)) {
                        requestToken = RequestToken.EMPTY;
                        if (message.dataString().isEmpty()) return Optional.empty();
                        return Optional.ofNullable(serializer.fromString(message.dataString(), clazz));
                    }
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
            reducedChange = change;
        } else {
            reducedChange = reducedChange.reduce(change);
        }
    }

    private void send(MessageType messageType, CHANGE change) {
        connection.send(new Message(messageType, serializer.toString(change)));
    }

    private void send(MessageType messageType, RequestToken requestToken, CHANGE change) {
        connection.send(new Message(messageType, requestToken, serializer.toString(change)));
    }

    private void send(MessageType messageType, RequestToken requestToken) {
        connection.send(new Message(messageType, requestToken, ""));
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

    @Override
    public boolean isConnected() {
        return connection.isConnected();
    }

    @AllArgsConstructor
    public static class Factory<CHANGE extends Change<?, CHANGE>> implements RemoteDataSource.Factory<CHANGE> {
        private final Serializer serializer;
        private final ClientConnectionDataSource clientConnectionDataSource;
        private final MessageType messageType;
        private final MessageType syncRequestType;
        private final MessageType syncResponseType;
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
