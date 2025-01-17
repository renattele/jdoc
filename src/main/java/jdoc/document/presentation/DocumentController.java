package jdoc.document.presentation;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import jdoc.core.di.Injected;
import jdoc.core.domain.source.RemoteDataSource;
import jdoc.core.presentation.Controller;
import jdoc.document.domain.source.LocalTextSource;
import jdoc.document.presentation.components.ConnectedStatusCard;
import jdoc.recent.domain.RecentDocument;
import jdoc.user.domain.UserRepository;
import jdoc.document.domain.DocumentRepository;
import jdoc.document.presentation.components.MarkdownTextArea;
import jdoc.document.data.source.TextAreaSource;
import jdoc.user.presentation.UserRow;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

@Slf4j
public class DocumentController extends Controller<RecentDocument> {
    @FXML
    private VBox container;

    @FXML
    private MarkdownTextArea textArea;

    @FXML
    private HBox topContainer;

    @Injected
    private DocumentRepository documentRepository;

    @Injected
    private UserRepository userRepository;

    @Injected
    private LocalTextSource.Factory localTextSourceFactory;

    @Override
    public void init() {
        VBox.setVgrow(container, Priority.ALWAYS);
        var areaTextSource = new TextAreaSource(textArea);
        var recentDocument = argument;
        var document = documentRepository.getRemoteDocument(recentDocument.remoteUrl());
        document.addSource(areaTextSource);
        var remoteDataSourceList = document
                .originalSources()
                .stream()
                .filter(source -> source instanceof RemoteDataSource<?>)
                .toList();
        if (!remoteDataSourceList.isEmpty()) {
            var remoteDataSource = (RemoteDataSource<?>) remoteDataSourceList.get(0);
            topContainer.getChildren().add(0, new ConnectedStatusCard(remoteDataSource::isConnected));
        }
        try {
            var emitFromFile = recentDocument.type() == RecentDocument.Type.Local;
            document.addSource(localTextSourceFactory.create(new File(recentDocument.localUrl()), emitFromFile));
        } catch (IOException e) {
            log.error(e.toString(), e);
        }
        var userList = userRepository.getUsersByUrl(recentDocument.remoteUrl());
        topContainer.getChildren().add(new UserRow(userList));
    }
}