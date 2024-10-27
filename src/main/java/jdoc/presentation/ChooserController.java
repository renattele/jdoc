package jdoc.presentation;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import jdoc.data.Settings;
import jdoc.presentation.components.RecentLocationButton;

import java.util.Map;
import java.util.stream.Collectors;

public class ChooserController extends Controller<Object> {
    @FXML
    private VBox container;

    @FXML
    private TextField connectField;

    @FXML
    private FlowPane recentContainer;

    @Override
    public void init() {
        updateRecentUrls();
        connectField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                Settings.addRecent(connectField.getText());
                updateRecentUrls();
            }
        });
    }

    private void updateRecentUrls() {
        var recent = new java.util.ArrayList<>(Settings
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
                Settings.deleteRecent(url);
                updateRecentUrls();
            });
            children.add(button);
        }
    }
}
