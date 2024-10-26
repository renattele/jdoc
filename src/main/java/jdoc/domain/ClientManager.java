package jdoc.domain;

import com.google.gson.Gson;
import io.reactivex.rxjava3.core.Flowable;

import java.util.List;
import java.util.Optional;

public class ClientManager {
    private final Client client;
    private final Gson gson;

    public ClientManager(Client client, Gson gson) {
        this.client = client;
        this.gson = gson;
    }

    public Flowable<List<ClientEntity>> allClients() {
        return client.incoming().mapOptional(message -> {
            if (message.type() == Message.GET_CLIENTS) {
                @SuppressWarnings("unchecked")
                var clients = (List<String>) gson.fromJson(message.dataString(), List.class);
                var clientEntities = clients.stream().map(ClientEntity::new).toList();
                return Optional.of(clientEntities);
            }
            return Optional.empty();
        });
    }
}
