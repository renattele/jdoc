package jdoc.core.net.client;

import io.reactivex.rxjava3.core.Flowable;
import jdoc.core.domain.Serializer;
import jdoc.core.net.protocol.ClientEntity;
import jdoc.core.net.protocol.Message;

import java.util.List;
import java.util.Optional;

public class ClientManagerImpl {
    private final ClientConnection clientConnection;
    private final Serializer serializer;

    public ClientManagerImpl(ClientConnection clientConnection, Serializer serializer) {
        this.clientConnection = clientConnection;
        this.serializer = serializer;
    }

    public Flowable<List<ClientEntity>> allClients() {
        return clientConnection.incoming().mapOptional(message -> {
            if (message.type() == Message.GET_CLIENTS) {
                @SuppressWarnings("unchecked")
                var clients = (List<String>) serializer.fromString(message.dataString(), List.class);
                var clientEntities = clients.stream().map(ClientEntity::new).toList();
                return Optional.of(clientEntities);
            }
            return Optional.empty();
        });
    }
}
