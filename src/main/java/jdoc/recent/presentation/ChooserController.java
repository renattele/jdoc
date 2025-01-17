package jdoc.recent.presentation;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import jdoc.core.di.Injected;
import jdoc.core.net.client.ClientConnectionDataSource;
import jdoc.core.presentation.Controller;
import jdoc.core.presentation.FileChooserOptions;
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
    private Text errorMessageText;

    @FXML
    private FlowPane recentContainer;

    @Injected
    private RecentDocumentsRepository recentDocumentsRepository;

    @Injected
    private ClientConnectionDataSource clientConnectionDataSource;

    @Override
    public void init() {
        updateRecentUrls();
        connectField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                var url = connectField.getText();
                if (!isConnectionAvailable(url)) return;
                var document = new RecentDocument(
                        RecentDocument.Type.Remote,
                        connectField.getText(),
                        null,
                        connectField.getText()
                );
                recentDocumentsRepository.addRecent(document);
                updateRecentUrls();
            } else {
                errorMessageText.setText("");
                errorMessageText.setVisible(false);
            }
        });
    }

    private boolean isConnectionAvailable(String url) {
        try (var ignored = clientConnectionDataSource.get(url)) {
        } catch (Exception e) {
            errorMessageText.setText("Unable to connect to " + url);
            errorMessageText.setVisible(true);
            return false;
        }
        return true;
    }

    private void updateRecentUrls() {
        var recent = new java.util.ArrayList<>(recentDocumentsRepository
                .getRecent()
                .stream()
                .toList());
        var children = recentContainer.getChildren();
        children.clear();
        var newDocumentButton = new RecentLocationButton("New", "icons/ic_add.png", false);
        newDocumentButton.setOnClick(event -> {
            createDocument();
        });
        var openDocumentButton = new RecentLocationButton("Open", "icons/ic_folder_open.png", false);
        openDocumentButton.setOnClick(event -> {
            openDocument();
        });
        children.add(0, openDocumentButton);
        children.add(0, newDocumentButton);
        for (var document : recent) {
            var button = getRecentLocationButton(document);
            children.add(button);
        }
    }

    private void createDocument() {
        var localFile = App.saveFile(markdownFileOptions());
        if (localFile.isEmpty()) return;
        var document = new RecentDocument(
                RecentDocument.Type.Local,
                "localhost",
                localFile.get().getAbsolutePath(),
                localFile.get().getName()
        );
        recentDocumentsRepository.addRecent(document);
        updateRecentUrls();
    }

    private void openDocument() {
        var localFile = App.openFile(markdownFileOptions());
        if (localFile.isEmpty()) return;
        var document = new RecentDocument(
                RecentDocument.Type.Local,
                "localhost",
                localFile.get().getAbsolutePath(),
                localFile.get().getName()
        );
        recentDocumentsRepository.addRecent(document);
        updateRecentUrls();
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
        Optional<File> localFile;
        if (document.type() == RecentDocument.Type.Remote) {
            if (!isConnectionAvailable(document.remoteUrl())) return;
            localFile = App.saveFile(markdownFileOptions());
        } else {
            localFile = Optional.of(new File(document.localUrl()));
        }
        if (localFile.isEmpty()) return;
        var modifiedDocument = document
                .toBuilder()
                .localUrl(localFile.get().getAbsolutePath())
                .build();
        App.navigate("/document-view.fxml", modifiedDocument);
    }

    private FileChooserOptions markdownFileOptions() {
        return FileChooserOptions.builder()
                .description("Markdown file")
                .extension("*.md")
                .build();
    }
}
