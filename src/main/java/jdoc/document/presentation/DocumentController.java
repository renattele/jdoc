package jdoc.document.presentation;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import jdoc.core.di.Injected;
import jdoc.core.net.server.ServerConnection;
import jdoc.core.presentation.Controller;
import jdoc.core.net.server.HostManagerImpl;
import jdoc.core.net.server.impl.SocketServerConnection;
import jdoc.core.domain.Serializer;
import jdoc.user.domain.UserRepository;
import jdoc.document.domain.DocumentRepository;
import jdoc.document.presentation.components.MarkdownTextArea;
import jdoc.document.data.source.TextAreaSource;
import jdoc.user.presentation.UserRow;

public class DocumentController extends Controller<String> {
    @FXML
    private VBox container;

    @FXML
    private MarkdownTextArea textArea;

    @FXML
    private HBox clientsContainer;

    @Injected
    private DocumentRepository documentRepository;

    @Injected
    private UserRepository userRepository;

    @Injected
    private Serializer serializer;

    @Override
    public void init() {
        VBox.setVgrow(container, Priority.ALWAYS);
        ServerConnection serverConnection = new SocketServerConnection(8080);
        new HostManagerImpl(serverConnection, serializer);
        documentRepository.getRemoteDocument(new TextAreaSource(textArea), argument);
        var userList = userRepository.getUsersByUrl(argument);
        clientsContainer.getChildren().add(new UserRow(userList));
    }
}