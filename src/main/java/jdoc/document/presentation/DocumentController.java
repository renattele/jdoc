package jdoc.document.presentation;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import jdoc.core.di.Injected;
import jdoc.core.net.client.ClientConnection;
import jdoc.core.net.server.ServerConnection;
import jdoc.core.presentation.Controller;
import jdoc.core.net.client.ClientManagerImpl;
import jdoc.core.net.server.HostManagerImpl;
import jdoc.core.net.client.impl.SocketClientConnection;
import jdoc.core.net.server.impl.SocketServerConnection;
import jdoc.core.domain.Serializer;
import jdoc.document.domain.User;
import jdoc.document.domain.UserRepository;
import jdoc.document.domain.DocumentRepository;
import jdoc.document.presentation.components.MarkdownTextArea;
import jdoc.document.presentation.components.UserAvatar;
import jdoc.document.data.source.text.TextAreaSource;

import java.io.IOException;
import java.net.URI;
import java.util.*;

@SuppressWarnings({"ResultOfMethodCallIgnored", "CallToPrintStackTrace"})
public class DocumentController extends Controller<String> {
    @FXML
    private VBox container;

    @FXML
    private MarkdownTextArea textArea;

    @FXML
    private HBox clientsContainer;

    @FXML
    private TextField clientName;

    @Injected
    private DocumentRepository documentRepository;

    @Injected
    private UserRepository.Factory userRepositoryFactory;
    private UserRepository userRepository;

    @Injected
    private Serializer serializer;

    @Override
    public void init() {
        VBox.setVgrow(container, Priority.ALWAYS);
        ServerConnection serverConnection = new SocketServerConnection(8080);
        new HostManagerImpl(serverConnection, serializer);
        documentRepository.getRemoteDocument(new TextAreaSource(textArea), argument);
        this.userRepository = userRepositoryFactory.create(argument);
        userRepository.users().subscribe(this::updateClientAvatars);
        clientName.textProperty().addListener(
                (observable, oldValue, newValue) ->
                        broadcastUsername(newValue)
        );
    }

    private void broadcastUsername(String newValue) {
        userRepository.setName(newValue);
    }

    private void updateClientAvatars(List<User> clients) {
        System.out.println(clients);
        Platform.runLater(() -> {
            var children = clientsContainer.getChildren();
            children.removeIf(node -> node instanceof UserAvatar);
            for (User clientEntity : clients) {
                children.add(0, new UserAvatar(clientEntity.name()));
            }
        });
    }
}