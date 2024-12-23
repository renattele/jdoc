package jdoc.document.presentation;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import jdoc.core.di.Injected;
import jdoc.core.presentation.Controller;
import jdoc.core.net.server.impl.SocketServerConnection;
import jdoc.document.domain.source.LocalTextSource;
import jdoc.recent.domain.RecentDocument;
import jdoc.user.domain.UserRepository;
import jdoc.document.domain.DocumentRepository;
import jdoc.document.presentation.components.MarkdownTextArea;
import jdoc.document.data.source.TextAreaSource;
import jdoc.user.presentation.UserRow;

import java.io.File;
import java.io.IOException;

public class DocumentController extends Controller<RecentDocument> {
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
    private LocalTextSource.Factory localTextSourceFactory;

    @Override
    public void init() {
        VBox.setVgrow(container, Priority.ALWAYS);
        new SocketServerConnection(8080);
        var areaTextSource = new TextAreaSource(textArea);
        var recentDocument = argument;
        var document = documentRepository.getRemoteDocument(recentDocument.remoteUrl());
        document.addSource(areaTextSource);
        try {
            document.addSource(localTextSourceFactory.create(new File(recentDocument.localUrl())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        var userList = userRepository.getUsersByUrl(recentDocument.remoteUrl());
        clientsContainer.getChildren().add(new UserRow(userList));
    }
}