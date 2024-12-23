package jdoc.recent.presentation;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import jdoc.core.di.Injected;
import jdoc.core.presentation.Controller;
import jdoc.recent.domain.RecentDocument;
import jdoc.recent.domain.RecentDocumentsRepository;
import jdoc.App;
import jdoc.recent.presentation.components.RecentLocationButton;

import java.io.File;
import java.util.Optional;

public class ChooserController extends Controller<Object> {
    @FXML
    private VBox container;

    @FXML
    private TextField connectField;

    @FXML
    private FlowPane recentContainer;

    @Injected
    private RecentDocumentsRepository recentDocumentsRepository;

    @Override
    public void init() {
        updateRecentUrls();
        connectField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                var document = new RecentDocument(
                        RecentDocument.Type.Remote,
                        connectField.getText(),
                        null,
                        connectField.getText()
                );
                recentDocumentsRepository.addRecent(document);
                updateRecentUrls();
            }
        });
    }

    private void updateRecentUrls() {
        var recent = new java.util.ArrayList<>(recentDocumentsRepository
                .getRecent()
                .stream()
                .toList());
        var newDocument = new RecentDocument(
                RecentDocument.Type.New,
                "localhost",
                null,
                "New"
        );
        recent.add(0, newDocument);
        var children = recentContainer.getChildren();
        children.clear();
        for (var document : recent) {
            var button = getRecentLocationButton(document);
            children.add(button);
        }
    }

    private RecentLocationButton getRecentLocationButton(RecentDocument document) {
        String leaderIcon;
        switch (document.type()) {
            case Local -> leaderIcon = "icons/ic_home.png";
            case Remote -> leaderIcon = "icons/ic_cloud.png";
            default -> leaderIcon = "icons/ic_add.png";
        }
        var button = new RecentLocationButton(document.displayName(), leaderIcon);
        button.setOnClick(event -> {
            onRecentDocumentClick(document);
        });
        button.setOnDelete(event -> {
            recentDocumentsRepository.deleteRecent(document.remoteUrl());
            updateRecentUrls();
        });
        return button;
    }

    private void onRecentDocumentClick(RecentDocument document) {
        var description = "Markdown file";
        var supportedType = "md";
        Optional<File> localFile = App.saveFile(description, supportedType);
        if (localFile.isEmpty()) return;
        var modifiedDocument = document
                .toBuilder()
                .localUrl(localFile.get().getAbsolutePath())
                .build();
        App.navigate("/document-view.fxml", modifiedDocument);
    }
}
