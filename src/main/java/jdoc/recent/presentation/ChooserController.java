package jdoc.recent.presentation;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import jdoc.core.di.Injected;
import jdoc.core.presentation.Controller;
import jdoc.recent.domain.RecentDocumentsRepository;
import jdoc.App;
import jdoc.recent.presentation.components.RecentLocationButton;

import java.util.Map;
import java.util.stream.Collectors;

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
                recentDocumentsRepository.addRecent(connectField.getText());
                updateRecentUrls();
            }
        });
    }

    private void updateRecentUrls() {
        var recent = new java.util.ArrayList<>(recentDocumentsRepository
                .getRecent()
                .stream()
                .collect(Collectors.toMap(i -> i, i -> i))
                .entrySet()
                .stream()
                .toList());
        recent.add(0, Map.entry("http://localhost", "Host"));
        var children = recentContainer.getChildren();
        children.clear();
        for (Map.Entry<String, String> entry : recent) {
            var url = entry.getKey();
            var name = entry.getValue();
            var button = new RecentLocationButton(name);
            button.setOnClick(event -> App.navigate("/document-view.fxml", url));
            button.setOnDelete(event -> {
                recentDocumentsRepository.deleteRecent(url);
                updateRecentUrls();
            });
            children.add(button);
        }
    }
}
